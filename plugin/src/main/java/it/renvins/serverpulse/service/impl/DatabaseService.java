package it.renvins.serverpulse.service.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import it.renvins.serverpulse.ServerPulseLoader;
import it.renvins.serverpulse.ServerPulsePlugin;
import it.renvins.serverpulse.config.CustomConfig;
import it.renvins.serverpulse.service.IDatabaseService;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

public class DatabaseService implements IDatabaseService {

    private final ServerPulsePlugin plugin;
    private final CustomConfig customConfig;

    @Getter private InfluxDBClient client;
    @Getter private WriteApi writeApi;

    private HttpClient httpClient; // Keep for ping
    private volatile BukkitTask retryTask; // volatile for visibility across threads

    private final int MAX_RETRIES = 5;
    private final long RETRY_DELAY_TICKS = 20L * 30L; // 30 seconds

    // Use volatile as this is read/written by different threads
    private volatile boolean isConnected = false;
    private volatile int retryCount = 0;

    public DatabaseService(ServerPulsePlugin plugin, CustomConfig customConfig) {
        this.plugin = plugin;
        this.customConfig = customConfig;
        this.httpClient = HttpClient.newBuilder()
                                    .connectTimeout(Duration.ofSeconds(10))
                                    .build();
    }

    @Override
    public void load() {
        if (!checkConnectionData()) {
            ServerPulseLoader.LOGGER.severe("InfluxDB connection data is missing or invalid. Disabling plugin.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::connect);
    }

    @Override
    public void unload() {
        stopRetryTask(); // Stop retries before disconnecting
        disconnect();
        if (httpClient != null) {
            httpClient.close();
        }
    }

    @Override
    public boolean isConnected() {
        // Return the flag, don't ping here!
        return this.isConnected;
    }

    /**
     * Attempts to connect to InfluxDB. Updates the internal connection status
     * and starts the retry task if connection fails.
     * Should be run asynchronously.
     */
    private void connect() {
        // If already connected, don't try again unless forced (e.g., by retry task)
        // Note: This check might prevent the retry task from running if isConnected is true
        // but the connection actually dropped without detection. We rely on ping() inside here.

        // Ensure previous resources are closed if attempting a new connection
        // This might be needed if connect() is called manually or by retry.
        if (client != null) {
            client.close();
            client = null;
        }
        if (writeApi != null) {
            writeApi.close();
            writeApi = null;
        }
        // Reset status before attempting connection
        this.isConnected = false;

        ConfigurationSection section = customConfig.getConfig().getConfigurationSection("metrics.influxdb");
        if (section == null) {
            ServerPulseLoader.LOGGER.severe("InfluxDB config section missing during connect attempt.");
            return;
        }
        String url = section.getString("url");
        String token = section.getString("token");
        String org = section.getString("org");
        String bucket = section.getString("bucket");

        try {
            ServerPulseLoader.LOGGER.info("Attempting to connect to InfluxDB at " + url);
            client = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);

            // Ping immediately after creating client to verify reachability & auth
            boolean isPingSuccessful = ping(); // Use the internal ping method

            if (isPingSuccessful) {
                writeApi = client.makeWriteApi(); // Initialize Write API

                this.isConnected = true;
                this.retryCount = 0; // Reset retry count on successful connection

                stopRetryTask(); // Stop retrying if we just connected
                ServerPulseLoader.LOGGER.info("Successfully connected to InfluxDB and ping successful.");
            } else {
                // Ping failed after client creation (maybe auth ok, but server busy/unhealthy?)
                ServerPulseLoader.LOGGER.warning("Connected to InfluxDB instance, but ping failed. Will retry.");
                this.isConnected = false; // Ensure status is false

                if (client != null) {
                    client.close(); // Close the client if ping failed
                    client = null;
                }
                startRetryTaskIfNeeded(); // Start retry task
            }
        } catch (Exception e) {
            // Handle exceptions during InfluxDBClientFactory.create() or ping()
            ServerPulseLoader.LOGGER.log(Level.SEVERE, "Failed to connect or ping InfluxDB: " + e.getMessage());
            this.isConnected = false;
            if (client != null) { // Ensure client is closed on exception
                client.close();
                client = null;
            }
            startRetryTaskIfNeeded(); // Start retry task
        }
    }


    /** Closes InfluxDB client and write API. */
    private void disconnect() {
        ServerPulseLoader.LOGGER.info("Disconnecting from InfluxDB...");
        if (writeApi != null) {
            try {
                writeApi.close();
            } catch (Exception e) {
                ServerPulseLoader.LOGGER.log(Level.WARNING, "Error closing InfluxDB WriteApi...", e);
            }
            writeApi = null;
        }
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                ServerPulseLoader.LOGGER.log(Level.WARNING, "Error closing InfluxDB Client...", e);
            }
            client = null;
        }
        this.isConnected = false;
    }


    /** Starts the retry task only if it's not already running. */
    private synchronized void startRetryTaskIfNeeded() {
        if (retryTask != null && !retryTask.isCancelled()) {
            return;
        }
        if (!plugin.isEnabled()) {
            ServerPulseLoader.LOGGER.warning("Plugin disabling, not starting retry task.");
            return;
        }

        // Reset retry count ONLY when starting the task sequence anew
        this.retryCount = 0;
        ServerPulseLoader.LOGGER.warning("Connection failed. Starting connection retry task (Max " + MAX_RETRIES + " attempts)...");

        retryTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            // Check connection status *first* using the flag
            if (this.isConnected) {
                ServerPulseLoader.LOGGER.info("Connection successful, stopping retry task.");
                stopRetryTask();
                return;
            }

            // Check if plugin got disabled externally
            if (!plugin.isEnabled()) {
                ServerPulseLoader.LOGGER.warning("Plugin disabled during retry task execution.");
                stopRetryTask();
                return;
            }

            // Check retries *before* attempting connection
            if (retryCount >= MAX_RETRIES) {
                ServerPulseLoader.LOGGER.severe("Max connection retries (" + MAX_RETRIES + ") reached. Disabling ServerPulse metrics.");
                stopRetryTask();
                disconnect(); // Clean up any partial connection
                // Schedule plugin disable on main thread
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    ServerPulseLoader.LOGGER.severe("Disabling ServerPulse plugin due to persistent connection failure.");
                    plugin.getServer().getPluginManager().disablePlugin(plugin);
                });
                return;
            }

            retryCount++;
            ServerPulseLoader.LOGGER.info("Retrying InfluxDB connection... Attempt " + retryCount + "/" + MAX_RETRIES);
            connect(); // Attempt to connect again

            // Note: connect() will handle setting isConnected flag and potentially stopping the task if successful.

        }, RETRY_DELAY_TICKS, RETRY_DELAY_TICKS); // Start after delay, repeat at delay
    }


    /** Stops and nullifies the retry task if it's running. */
    private synchronized void stopRetryTask() {
        if (retryTask != null) {
            if (!retryTask.isCancelled()) {
                try {
                    retryTask.cancel();
                } catch (Exception e) {
                    // Ignore potential errors during cancellation
                }
            }
            retryTask = null;
        }
    }

    /**
     * Performs a health check ping to the InfluxDB instance.
     * Should only be called internally by connect() or dedicated health checks.
     * @return true if ping is successful (HTTP 204), false otherwise.
     */
    private boolean ping() {
        String url = customConfig.getConfig().getString("metrics.influxdb.url");
        if (url == null || url.isEmpty()) {
            ServerPulseLoader.LOGGER.severe("InfluxDB URL is missing for ping.");
            return false;
        }

        // Ensure httpClient is initialized
        if (this.httpClient == null) {
            ServerPulseLoader.LOGGER.severe("HttpClient not initialized for ping.");
            // Re-initialize as a fallback, though it should be done in constructor
            this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        }

        HttpRequest request;
        try {
            String pingUrl = url.endsWith("/") ? url + "ping" : url + "/ping";
            request = HttpRequest.newBuilder()
                                 .uri(URI.create(pingUrl))
                                 .GET()
                                 .timeout(Duration.ofSeconds(5)) // Add timeout specific to ping
                                 .build();
        } catch (IllegalArgumentException e) {
            ServerPulseLoader.LOGGER.log(Level.SEVERE, "Invalid InfluxDB URL format for ping: " + url, e);
            return false;
        }


        try {
            HttpResponse<Void> response = this.httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            int statusCode = response.statusCode();

            if (statusCode == 204) {
                return true;
            } else {
                ServerPulseLoader.LOGGER.warning("InfluxDB ping failed: Received HTTP status code " + statusCode + " (expected 204).");
                return false;
            }
        } catch (Exception e) { // Catch broader exceptions
            ServerPulseLoader.LOGGER.log(Level.SEVERE, "Error during InfluxDB ping to " + url + ": " + e.getMessage());
            return false;
        }
    }


    /**
     * Checks if the essential connection data is present in the config.
     * @return true if data seems present, false otherwise.
     */
    private boolean checkConnectionData() {
        ConfigurationSection section = customConfig.getConfig().getConfigurationSection("metrics.influxdb");
        if (section == null) {
            ServerPulseLoader.LOGGER.severe("Missing 'metrics.influxdb' section in config.");
            return false;
        }
        String url = section.getString("url");
        String bucket = section.getString("bucket");
        String org = section.getString("org");
        String token = section.getString("token");

        boolean valid = true;
        if (url == null || url.isEmpty()) {
            ServerPulseLoader.LOGGER.severe("Missing or empty 'metrics.influxdb.url' in config...");
            valid = false;
        }
        if (bucket == null || bucket.isEmpty()) {
            ServerPulseLoader.LOGGER.severe("Missing or empty 'metrics.influxdb.bucket' in config...");
            valid = false;
        }
        if (org == null || org.isEmpty()) {
            ServerPulseLoader.LOGGER.severe("Missing or empty 'metrics.influxdb.org' in config...");
            valid = false;
        }
        if (token == null || token.isEmpty() || token.equals("my-token")) {
            ServerPulseLoader.LOGGER.severe("Missing, empty, or default 'metrics.influxdb.token' in config...");
            valid = false;
        }
        return valid;
    }
}