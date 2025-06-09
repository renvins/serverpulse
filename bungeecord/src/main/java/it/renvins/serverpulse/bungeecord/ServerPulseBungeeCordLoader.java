package it.renvins.serverpulse.bungeecord;

import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.api.service.Service;
import it.renvins.serverpulse.bungeecord.config.BungeeCordConfiguration;
import it.renvins.serverpulse.bungeecord.config.BungeeCordDatabaseConfiguration;
import it.renvins.serverpulse.bungeecord.config.BungeeCordMetricsConfiguration;
import it.renvins.serverpulse.bungeecord.logger.BungeeCordLogger;
import it.renvins.serverpulse.bungeecord.metrics.BungeeCordPingRetriever;
import it.renvins.serverpulse.bungeecord.platform.BungeeCordPlatform;
import it.renvins.serverpulse.bungeecord.scheduler.BungeeCordTaskScheduler;
import it.renvins.serverpulse.common.DatabaseService;
import it.renvins.serverpulse.common.MetricsService;
import it.renvins.serverpulse.common.config.DatabaseConfiguration;
import it.renvins.serverpulse.common.config.MetricsConfiguration;
import it.renvins.serverpulse.common.logger.PulseLogger;
import it.renvins.serverpulse.common.metrics.DiskRetriever;
import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;

import java.util.logging.Logger;

public class ServerPulseBungeeCordLoader implements Service {

    private final ServerPulseBungeeCord plugin;

    private final BungeeCordConfiguration config;
    private final Platform platform;

    private final IDatabaseService databaseService;
    private final IMetricsService metricsService;

    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    public ServerPulseBungeeCordLoader(ServerPulseBungeeCord plugin) {
        this.plugin = plugin;

        this.config = new BungeeCordConfiguration(plugin, "config.yml");
        this.platform = new BungeeCordPlatform(plugin);

        PulseLogger pulseLogger = new BungeeCordLogger(plugin);
        TaskScheduler scheduler = new BungeeCordTaskScheduler(plugin);

        DatabaseConfiguration dbConfig = new BungeeCordDatabaseConfiguration(config);
        MetricsConfiguration metricsConfig = new BungeeCordMetricsConfiguration(config);

        this.databaseService = new DatabaseService(pulseLogger, platform, dbConfig, scheduler);
        this.metricsService = new MetricsService(pulseLogger, platform, metricsConfig, scheduler);

        this.diskRetriever = new DiskRetriever(plugin.getDataFolder());
        this.pingRetriever =new BungeeCordPingRetriever(plugin);


    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {
        Service.super.unload();
    }
}
