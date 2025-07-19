package it.renvins.serverpulse.common.config;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MetricsConfiguration {

    private final GeneralConfiguration configuration;

    public String getServerTag() {
        return configuration.getConfig().getString("metrics.tags.server");
    }

    public String getMeasurementTable() {
        return configuration.getConfig().getString("metrics.influxdb.table");
    }

    public long getMetricsInterval() {
        return configuration.getConfig().getLong("metrics.interval");
    }

    public Map<String, String> getTags() {
        Map<String, Object> tags = configuration.getConfig().getConfigurationSection("metrics.tags").getValues(false);
        Map<String, String> stringTags = new HashMap<>();
        tags.forEach((key, value) -> {
            if (value instanceof String && !key.equalsIgnoreCase("server") && !key.equalsIgnoreCase("world")) {
                stringTags.put(key, (String) value);
            }
        });
        return stringTags;
    }
}
