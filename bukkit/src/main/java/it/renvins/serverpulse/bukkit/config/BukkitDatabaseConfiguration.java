package it.renvins.serverpulse.bukkit.config;

import it.renvins.serverpulse.common.config.DatabaseConfiguration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BukkitDatabaseConfiguration implements DatabaseConfiguration {

    private final BukkitConfiguration configuration;

    @Override
    public String getHost() {
        return configuration.getConfig().getString("metrics.influxdb.url");
    }

    @Override
    public String getOrg() {
        return configuration.getConfig().getString("metrics.influxdb.org");
    }

    @Override
    public String getToken() {
        return configuration.getConfig().getString("metrics.influxdb.token");
    }

    @Override
    public String getBucket() {
        return configuration.getConfig().getString("metrics.influxdb.bucket");
    }
}
