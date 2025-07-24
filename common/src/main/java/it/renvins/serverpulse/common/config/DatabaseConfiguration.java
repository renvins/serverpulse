package it.renvins.serverpulse.common.config;

import lombok.RequiredArgsConstructor;

/**
 * Configuration class for InfluxDB database settings.
 * This class retrieves the database connection details from the general configuration.
 */
@RequiredArgsConstructor
public class DatabaseConfiguration {

    private final GeneralConfiguration configuration;

    public String getHost() {
        return configuration.getConfig().getString("metrics.influxdb.url");
    }

    public String getOrg() {
        return configuration.getConfig().getString("metrics.influxdb.org");
    }

    public String getToken() {
        return configuration.getConfig().getString("metrics.influxdb.token");
    }

    public String getBucket() {
        return configuration.getConfig().getString("metrics.influxdb.bucket");
    }

}
