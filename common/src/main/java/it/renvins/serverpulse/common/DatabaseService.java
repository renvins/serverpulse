package it.renvins.serverpulse.common;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.common.config.DatabaseConfiguration;
import it.renvins.serverpulse.common.config.GeneralConfiguration;
import it.renvins.serverpulse.common.logger.PulseLogger;
import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.common.scheduler.Task;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;

public class DatabaseService implements IDatabaseService {

    private final PulseLogger logger;
    private final Platform platform;

    private final DatabaseConfiguration configuration;
    private final TaskScheduler scheduler;

    private HttpClient httpClient; // Keep for ping

    private volatile Task retryTask; // volatile for visibility across threads

    private final int MAX_RETRIES = 5;
    private final long RETRY_DELAY_MS = 30000L; // 30 seconds

    // Use volatile as this is read/written by different threads
    private volatile boolean isConnected = false;
    private volatile int retryCount = 0;

    //HTTP API endpoints
    private String pingUrl;
    private String writeUrl;

    public DatabaseService(PulseLogger logger, Platform platform, GeneralConfiguration generalConfig, TaskScheduler scheduler) {
        this.logger = logger;
        this.platform = platform;

        this.configuration = new DatabaseConfiguration(generalConfig);
        this.scheduler = scheduler;

        this.httpClient = HttpClient.newBuilder()
                                    .connectTimeout(Duration.ofSeconds(10))
                                    .build();
    }

    @Override
    public void load() {
        if (!checkConnectionData()) {
            logger.error("InfluxDB connection data is missing or invalid. Shutting down...");
            platform.disable();
            return;
        }
        logger.info("Connecting to InfluxDB...");

        // Initialize the HttpClient for ping
        String baseUrl = configuration.getHost();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        this.pingUrl = baseUrl + "ping";
        this.writeUrl = baseUrl + "api/v2/write?org=" + configuration.getOrg() +
                "&bucket=" + configuration.getBucket() + "&precision=ns";

        scheduler.runAsync(this::connect);
    }

    @Override
    public void unload() {
        logger.info("Unloading InfluxDB connection...");
        stopRetryTask(); // Stop retries before disconnecting
        disconnect();
        if (httpClient != null) {
            httpClient.close();
        }
    }

    @Override
    public CompletableFuture<Boolean> writeLineProtocol(String lineProtocol) {
        if (!isConnected) {
            return CompletableFuture.completedFuture(false);
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create(writeUrl))
                                             .header("Authorization", "Token " + configuration.getToken())
                                             .header("Content-Type", "text/plain; charset=utf-8")
                                             .POST(HttpRequest.BodyPublishers.ofString(lineProtocol, StandardCharsets.UTF_8))
                                             .timeout(Duration.ofSeconds(10))
                                             .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                             .thenApply(response -> {
                                 if (response.statusCode() == 204) {
                                     return true;
                                 } else {
                                     logger.error("Failed to write to InfluxDB: " + response.statusCode() + " - " + response.body());
                                     return false;
                                 }
                             })
                             .exceptionally(throwable -> {
                                 logger.error("Error writing to InfluxDB: " + throwable.getMessage());
                                 if (throwable.getCause() instanceof java.net.ConnectException) {
                                     // Connection lost, trigger reconnection
                                     this.isConnected = false;
                                     startRetryTaskIfNeeded();
                                 }
                                 return false;
                             });
        } catch (Exception e) {
            logger.error("Failed to create write request: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    public boolean ping() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create(pingUrl))
                                             .GET()
                                             .timeout(Duration.ofSeconds(5))
                                             .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 204;
        } catch (Exception e) {
            logger.error("InfluxDB ping failed", e);
            return false;
        }
    }

    @Override
    public void disconnect() {
        this.isConnected = false;
    }

    @Override
    public boolean isConnected() {
        // Return the flag, don't ping here!
        return this.isConnected;
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
                logger.error("Max connection retries (" + MAX_RETRIES + ") reached. Disabling ServerPulse metrics...");
                stopRetryTask();
                disconnect(); // Clean up any partial connection
                // Schedule plugin disable on main thread
                scheduler.runSync(platform::disable);
                return;
            }
            retryCount++;

            logger.info("Retrying InfluxDB connection... Attempt " + retryCount + "/" + MAX_RETRIES);
            connect(); // Note: connect() will handle setting isConnected flag and potentially stopping the task if successful.
        }, RETRY_DELAY_MS, RETRY_DELAY_MS); // Start after delay, repeat at delay
    }


    /**
     * Attempts to connect to InfluxDB. Updates the internal connection status
     * and starts the retry task if connection fails.
     * Should be run asynchronously.
     */
    private void connect() {
        disconnect();

        try {
            logger.info("Attempting to connect to InfluxDB via HTTP API...");
            boolean isPingSuccessful = ping(); // Use the internal ping method

            if (isPingSuccessful) {
                this.isConnected = true;
                this.retryCount = 0; // Reset retry count on successful connection

                stopRetryTask(); // Stop retrying if we just connected
                logger.info("Successfully connected to InfluxDB and ping successful...");
            } else {
                logger.warning("Ping failed. Will retry...");
                this.isConnected = false; // Ensure status is false
                startRetryTaskIfNeeded(); // Start retry task
            }
        } catch (Exception e) {
            logger.error("Failed to connect or ping InfluxDB: " + e.getMessage());
            this.isConnected = false;
            startRetryTaskIfNeeded(); // Start retry task
        }
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
            logger.error("Missing or empty 'metrics.influxdb.url' in config...");
            valid = false;
        }
        if (bucket == null || bucket.isEmpty()) {
            logger.error("Missing or empty 'metrics.influxdb.bucket' in config...");
            valid = false;
        }
        if (org == null || org.isEmpty()) {
            logger.error("Missing or empty 'metrics.influxdb.org' in config...");
            valid = false;
        }
        if (token == null || token.isEmpty() || token.equals("my-token")) {
            logger.error("Missing, empty, or default 'metrics.influxdb.token' in config...");
            valid = false;
        }
        return valid;
    }
}