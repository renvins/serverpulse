package it.renvins.serverpulse.task;

import java.time.Instant;
import java.util.Map;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import it.renvins.serverpulse.ServerPulseLoader;
import it.renvins.serverpulse.ServerPulsePlugin;
import it.renvins.serverpulse.config.CustomConfig;
import it.renvins.serverpulse.service.IDatabaseService;
import it.renvins.serverpulse.service.IMetricsService;
import org.bukkit.Bukkit;

public class MetricsTask implements Runnable {

    private final ServerPulsePlugin plugin;
    private final CustomConfig config;

    private final IDatabaseService databaseService;
    private final IMetricsService metricsService;

    public MetricsTask(ServerPulsePlugin plugin, CustomConfig config,
                       IDatabaseService databaseService, IMetricsService metricsService) {
        this.plugin = plugin;
        this.config = config;

        this.databaseService = databaseService;
        this.metricsService = metricsService;
    }

    @Override
    public void run() {
        // !-- THIS CODE IS RUNNING ASYNC --!
        if (!plugin.isEnabled() || databaseService.getWriteApi() == null) {
            return;
        }
        double[] tps = plugin.getServer().getTPS();

        Point point = Point.measurement("minecraft_stats") // it's like the name of the table
                           .addTag("server", config.getConfig().getString("metrics.tags.server"))
                           .addField("tps_1m", tps[0])
                           .addField("tps_5m", tps[1])
                           .addField("tps_15m", tps[2])
                           .addField("players_online", Bukkit.getOnlinePlayers().size())
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
        metricsService.writePoint(point);
        ServerPulseLoader.LOGGER.info("Metrics sent to InfluxDB..."); // just for testing
    }
}
