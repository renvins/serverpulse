package it.renvins.serverpulse.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;

public interface IDatabaseService extends Service {

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
     * Connects to the InfluxDB instance using the configured settings.
     * This method should be called before performing any database operations.
     */
    void disconnect();

    /**
     * Disconnects from the InfluxDB instance and cleans up resources.
     * This method should be called when the application is shutting down
     * or when the connection is no longer needed.
     */
    void startRetryTaskIfNeeded();

    /**
     * Gets the configured InfluxDB client instance.
     *
     * @return The InfluxDBClient.
     */
    InfluxDBClient getClient();

    /**
     * Gets the InfluxDB Write API instance for sending data points.
     *
     * @return The WriteApi.
     */
    WriteApi getWriteApi();
}
