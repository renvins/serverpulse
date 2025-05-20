package it.renvins.serverpulse.api.service;

import java.util.concurrent.CompletableFuture;

public interface IDatabaseService extends Service {

    /**
     * Writes a line protocol string to the InfluxDB instance.
     * @param lineProtocol The line protocol string to write.
     * @return A CompletableFuture that completes with true if the write was successful, false otherwise.
     */
    CompletableFuture<Boolean> writeLineProtocol(String lineProtocol);

    /**
     * Performs a health check ping to the InfluxDB instance.
     * Should only be called internally by connect() or dedicated health checks.
     * @return true if ping is successful (HTTP 204), false otherwise.
     */
    boolean ping();

    /**
     * Returns the last known connection status. Does not perform a live check.
     * @return true if the service believes it's connected, false otherwise.
     */
    boolean isConnected();

    /**
     * Disconnects from the InfluxDB instance and cleans up resources.
     * This method should be called when the application is shutting down
     * or when the connection is no longer needed.
     */
    void startRetryTaskIfNeeded();

}
