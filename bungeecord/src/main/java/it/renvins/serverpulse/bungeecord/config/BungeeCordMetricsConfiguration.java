package it.renvins.serverpulse.bungeecord.config;

import it.renvins.serverpulse.common.config.MetricsConfiguration;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class BungeeCordMetricsConfiguration implements MetricsConfiguration {

    private final BungeeCordConfiguration configuration;

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
        Map<String, String> stringTags = new HashMap<>();
        Configuration tagsSection = configuration.getConfig().getSection("metrics.tags");
        if (tagsSection != null) {
            for (String key : tagsSection.getKeys()) {
                Object value = tagsSection.get(key);
                if (value instanceof String && !key.equalsIgnoreCase("server") && !key.equalsIgnoreCase("world")) {
                    stringTags.put(key, (String) value);
                }
            }
        }
        return stringTags;
    }
}
