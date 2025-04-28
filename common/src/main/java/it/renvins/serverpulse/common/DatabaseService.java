package it.renvins.serverpulse.common;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.common.config.DatabaseConfiguration;
import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.common.scheduler.Task;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;
import lombok.Getter;

public class DatabaseService implements IDatabaseService {

    private final Logger logger;
    private final Platform platform;

    private final DatabaseConfiguration configuration;
    private final TaskScheduler scheduler;

    private HttpClient httpClient; // Keep for ping

    @Getter private InfluxDBClient client;
    @Getter private WriteApi writeApi;

    private volatile Task retryTask; // volatile for visibility across threads

    private final int MAX_RETRIES = 5;
    private final long RETRY_DELAY_TICKS = 20L * 30L; // 30 seconds

    // Use volatile as this is read/written by different threads
    private volatile boolean isConnected = false;
    private volatile int retryCount = 0;

    public DatabaseService(Logger logger, Platform platform, DatabaseConfiguration configuration, TaskScheduler scheduler) {
        this.logger = logger;
        this.platform = platform;

        this.configuration = configuration;

        this.scheduler = scheduler;

        this.httpClient = HttpClient.newBuilder()
                                    .connectTimeout(Duration.ofSeconds(10))
                                    .build();
    }

    @Override
    public void load() {
        if (!checkConnectionData()) {
            logger.severe("InfluxDB connection data is missing or invalid. Disabling plugin...");
            platform.disable();
            return;
        }
        logger.info("Connecting to InfluxDB...");
        scheduler.runAsync(this::connect);
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
    public boolean ping() {
        String url = configuration.getHost();
        if (url == null || url.isEmpty()) {
            logger.severe("InfluxDB URL is missing for ping...");
            return false;
        }

        // Ensure httpClient is initialized
        if (this.httpClient == null) {
            logger.severe("HttpClient not initialized for ping...");
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
            logger.log(Level.SEVERE, "Invalid InfluxDB URL format for ping: " + url, e);
            return false;
        }

        try {
            HttpResponse<Void> response = this.httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 204;
        } catch (java.net.ConnectException | java.net.UnknownHostException e) {
            logger.warning("InfluxDB service is offline...");
            return false;
        } catch (
                SocketTimeoutException e) {
            logger.warning("InfluxDB ping timed out: " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during InfluxDB ping: " + e.getMessage(), e);
            return false;
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
        disconnect();

        String url = configuration.getHost();
        String token = configuration.getToken();
        String org = configuration.getOrg();
        String bucket = configuration.getBucket();

        try {
            logger.info("Attempting to connect to InfluxDB at " + url);
            client = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);

            // Ping immediately after creating client to verify reachability & auth
            boolean isPingSuccessful = ping(); // Use the internal ping method

            if (isPingSuccessful) {
                writeApi = client.makeWriteApi(); // Initialize Write API

                this.isConnected = true;
                this.retryCount = 0; // Reset retry count on successful connection

                stopRetryTask(); // Stop retrying if we just connected
                logger.info("Successfully connected to InfluxDB and ping successful...");
            } else {
                // Ping failed after client creation
                logger.warning("Created InfluxDB instance, but ping failed. Will retry...");
                this.isConnected = false; // Ensure status is false

                if (client != null) {
                    client.close(); // Close the client if ping failed
                    client = null;
                }
                startRetryTaskIfNeeded(); // Start retry task
            }
        } catch (Exception e) {
            // Handle exceptions during InfluxDBClientFactory.create() or ping()
            logger.log(Level.SEVERE, "Failed to connect or ping InfluxDB: " + e.getMessage());
            this.isConnected = false;
            if (client != null) { // Ensure client is closed on exception
                client.close();
                client = null;
            }
            startRetryTaskIfNeeded(); // Start retry task
        }
    }

    @Override
    public void disconnect() {
        if (writeApi != null) {
            try {
                writeApi.close();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error closing InfluxDB WriteApi...", e);
            }
            writeApi = null;
        }
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error closing InfluxDB Client...", e);
            }
            client = null;
        }
        this.isConnected = false;
    }


    @Override
    public synchronized void startRetryTaskIfNeeded() {
        if (retryTask != null && !retryTask.isCancelled()) {
            return;
        }
        if (!platform.isEnabled()) {
            logger.warning("Plugin disabling, not starting retry task...");
            return;
        }

        // Reset retry count ONLY when starting the task sequence
        this.retryCount = 0;
        logger.warning("Connection failed. Starting connection retry task (Max " + MAX_RETRIES + " attempts)...");

        retryTask = scheduler.runTaskTimerAsync(() -> {
            // Check connection status *first* using the flag
            if (this.isConnected) {
                logger.info("Connection successful, stopping retry task...");
                stopRetryTask();
                return;
            }

            // Check if plugin got disabled externally
            if (!platform.isEnabled()) {
                logger.warning("Plugin disabled during retry task execution...");
                stopRetryTask();
                return;
            }

            // Check retries *before* attempting connection
            if (retryCount >= MAX_RETRIES) {
                logger.severe("Max connection retries (" + MAX_RETRIES + ") reached. Disabling ServerPulse metrics...");
                stopRetryTask();
                disconnect(); // Clean up any partial connection
                // Schedule plugin disable on main thread
                scheduler.runSync(platform::disable);
                return;
            }
            retryCount++;

            logger.info("Retrying InfluxDB connection... Attempt " + retryCount + "/" + MAX_RETRIES);
            connect(); // Note: connect() will handle setting isConnected flag and potentially stopping the task if successful.
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
     * Checks if the essential connection data is present in the config.
     * @return true if data seems present, false otherwise.
     */
    private boolean checkConnectionData() {
        String url = configuration.getHost();
        String token = configuration.getToken();
        String org = configuration.getOrg();
        String bucket = configuration.getBucket();

        boolean valid = true;
        if (url == null || url.isEmpty()) {
            logger.severe("Missing or empty 'metrics.influxdb.url' in config...");
            valid = false;
        }
        if (bucket == null || bucket.isEmpty()) {
            logger.severe("Missing or empty 'metrics.influxdb.bucket' in config...");
            valid = false;
        }
        if (org == null || org.isEmpty()) {
            logger.severe("Missing or empty 'metrics.influxdb.org' in config...");
            valid = false;
        }
        if (token == null || token.isEmpty() || token.equals("my-token")) {
            logger.severe("Missing, empty, or default 'metrics.influxdb.token' in config...");
            valid = false;
        }
        return valid;
    }
}