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
import it.renvins.serverpulse.metrics.IDiskRetriever;
import it.renvins.serverpulse.metrics.IMemoryRetriever;
import it.renvins.serverpulse.metrics.impl.DiskRetriever;
import it.renvins.serverpulse.metrics.impl.MemoryRetriever;
import it.renvins.serverpulse.service.IDatabaseService;
import it.renvins.serverpulse.service.IMetricsService;
import it.renvins.serverpulse.task.MetricsTask;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

public class MetricsService implements IMetricsService {

    private final ServerPulsePlugin plugin;
    private final CustomConfig config;

    private final IDatabaseService databaseService;

    private final IMemoryRetriever memoryRetriever;
    private final IDiskRetriever diskRetriever;

    public MetricsService(ServerPulsePlugin plugin, CustomConfig config, IDatabaseService databaseService) {
        this.plugin = plugin;
        this.config = config;
        this.databaseService = databaseService;

        this.memoryRetriever = new MemoryRetriever();
        this.diskRetriever = new DiskRetriever(plugin.getDataFolder());
    }

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

            Point point = Point.measurement(config.getConfig().getString("metrics.influxdb.table")) // it's like the name of the table
                               .addTag("server", config.getConfig().getString("metrics.tags.server"))
                               .addField("tps_1m", tps[0])
                               .addField("tps_5m", tps[1])
                               .addField("tps_15m", tps[2])
                               .addField("players_online", Bukkit.getOnlinePlayers().size())
                               .addField("used_memory", memoryRetriever.getUsedHeapBytes())
                               .addField("available_memory", memoryRetriever.getCommittedHeapBytes())
                                 .addField("total_disk_space", diskRetriever.getTotalSpace())
                                 .addField("usable_disk_space", diskRetriever.getUsableSpace())
                               // we're going to add other fields, now we are just testing
                               .time(Instant.now(), WritePrecision.NS);

            // add other tags from the configuration
            Map<String, Object> tags = config.getConfig().getConfigurationSection("metrics.tags").getValues(false);
            tags.forEach((key, value) -> {
                if (value instanceof String && !key.equals("server")) {
                    point.addTag(key, value.toString());
                }
            });
            databaseService.getWriteApi().writePoint(point);
        } catch (Exception e) {
            ServerPulseLoader.LOGGER.severe("Error while sending metrics: " + e.getMessage());
        }
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
