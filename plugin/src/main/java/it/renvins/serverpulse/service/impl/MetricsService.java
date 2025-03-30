package it.renvins.serverpulse.service.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.Instant;
import java.util.Map;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import it.renvins.serverpulse.ServerPulseLoader;
import it.renvins.serverpulse.ServerPulsePlugin;
import it.renvins.serverpulse.config.CustomConfig;
import it.renvins.serverpulse.service.IDatabaseService;
import it.renvins.serverpulse.service.IMetricsService;
import it.renvins.serverpulse.task.MetricsTask;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
public class MetricsService implements IMetricsService {

    private final ServerPulsePlugin plugin;
    private final CustomConfig config;

    private final IDatabaseService databaseService;

    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    @Override
    public void load() {
        ServerPulseLoader.LOGGER.info("Loading metrics task...");
        loadTask();
    }

    @Override
    public void sendMetrics() {
        if (databaseService.getWriteApi() == null) {
            return;
        }
        try {
            double[] tps = plugin.getServer().getTPS();

            Point point = Point.measurement("minecraft_stats") // it's like the name of the table
                               .addTag("server", config.getConfig().getString("metrics.tags.server"))
                               .addField("tps_1m", tps[0])
                               .addField("tps_5m", tps[1])
                               .addField("tps_15m", tps[2])
                               .addField("players_online", Bukkit.getOnlinePlayers().size())
                               .addField("used_memory", getUsedHeap())
                               .addField("available_memory", getAvailable())
                               // we're going to add other fields, now we are just testing
                               .time(Instant.now(), WritePrecision.NS);

            // add other tags from the configuration
            Map<String, Object> tags = config.getConfig().getConfigurationSection("metrics.tags").getValues(false);
            if (tags != null) {
                tags.forEach((key, value) -> {
                    if (value instanceof String && !key.equals("server")) {
                        point.addTag(key, value.toString());
                    }
                });
            }
            databaseService.getWriteApi().writePoint(point);
        } catch (Exception e) {
            ServerPulseLoader.LOGGER.severe("Error while sending metrics: " + e.getMessage());
        }
    }

    @Override
    public double getUsedHeap() {
        long used = memoryMXBean.getHeapMemoryUsage().getUsed();
        return used / (1024.0 * 1024.0); // Convert to MB
    }

    @Override
    public double getAvailable() {
        double maxHeap = getMaxHeap();
        if (maxHeap < 0) {
            return -1.0; // Indicate that the available memory cannot be calculated
        }
        return maxHeap - getUsedHeap();
    }

    /**
     * Retrieves the maximum configured heap memory (Xmx).
     *
     * @return Maximum heap memory in megabytes (MB), or -1.0 if it's not explicitly set or unlimited.
     */
    private double getMaxHeap() {
        long max = memoryMXBean.getHeapMemoryUsage().getMax();
        if (max == Long.MAX_VALUE || max < 0) {
            return -1.0; // Indicate that the max heap size is not set
        }
        return max / (1024.0 * 1024.0); // Convert to MB
    }

    /**
     * Schedules the {@link MetricsTask} to run asynchronously at a fixed interval
     * defined in the configuration (defaults usually involve ticks, e.g., 20L * interval_in_seconds).
     */
    private void loadTask() {
        MetricsTask task = new MetricsTask(plugin, this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, 0L, 20L * 5L);
    }
}
