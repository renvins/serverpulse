package it.renvins.serverpulse.paper.config;

import java.util.HashMap;
import java.util.Map;

import it.renvins.serverpulse.common.config.MetricsConfiguration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaperMetricsConfiguration implements MetricsConfiguration {

    private final PaperConfiguration configuration;

    @Override
    public String getServerTag() {
        return configuration.getConfig().getString("metrics.tags.server");
    }

    @Override
    public String getMeasurementTable() {
        return configuration.getConfig().getString("metrics.influxdb.table");
    }

    @Override
    public long getMetricsInterval() {
        return configuration.getConfig().getLong("metrics.interval");
    }

    @Override
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
