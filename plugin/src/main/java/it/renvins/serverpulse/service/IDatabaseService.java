package it.renvins.serverpulse.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;

public interface IDatabaseService extends Service {

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
