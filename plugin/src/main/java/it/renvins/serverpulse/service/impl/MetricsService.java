package it.renvins.serverpulse.service.impl;

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

    @Override
    public void load() {
        ServerPulseLoader.LOGGER.info("Loading metrics task...");
        loadTask();
    }

    @Override
    public void writePoint(Point point) {
        if (databaseService.getWriteApi() != null) {
            databaseService.getWriteApi().writePoint(point);
        } else {
            plugin.getLogger().warning("Write API is null, cannot write the point...");
        }
    }

    private void loadTask() {
        MetricsTask task = new MetricsTask(plugin, config, databaseService, this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, 0L, 20L * 5L);
    }
}
