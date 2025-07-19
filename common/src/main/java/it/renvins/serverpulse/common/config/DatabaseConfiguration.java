package it.renvins.serverpulse.common.config;

import lombok.RequiredArgsConstructor;

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
