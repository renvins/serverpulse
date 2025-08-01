package it.renvins.serverpulse.bungeecord;

import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IMSPTRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.api.service.Service;
import it.renvins.serverpulse.bungeecord.commands.ServerPulseCommand;
import it.renvins.serverpulse.bungeecord.logger.BungeeCordLogger;
import it.renvins.serverpulse.bungeecord.metrics.BungeeCordPingRetriever;
import it.renvins.serverpulse.bungeecord.platform.BungeeCordPlatform;
import it.renvins.serverpulse.bungeecord.scheduler.BungeeCordTaskScheduler;
import it.renvins.serverpulse.common.DatabaseService;
import it.renvins.serverpulse.common.MetricsService;
import it.renvins.serverpulse.common.config.GeneralConfiguration;
import it.renvins.serverpulse.common.logger.PulseLogger;
import it.renvins.serverpulse.common.disk.DiskRetriever;
import it.renvins.serverpulse.common.metrics.LineProtocolFormatter;
import it.renvins.serverpulse.common.metrics.MetricsCollector;
import it.renvins.serverpulse.common.metrics.UnsupportedMSPTRetriever;
import it.renvins.serverpulse.common.metrics.UnsupportedTPSRetriever;
import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ServerPulseBungeeCordLoader implements Service {

    private final ServerPulseBungeeCord plugin;
    public static Logger LOGGER;

    private final GeneralConfiguration config;
    private final Platform platform;

    private final IDatabaseService databaseService;

    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    private final IMetricsService metricsService;

    public ServerPulseBungeeCordLoader(ServerPulseBungeeCord plugin) {
        this.plugin = plugin;
        LOGGER = plugin.getLogger();

        PulseLogger pulseLogger = new BungeeCordLogger(plugin);

        this.config = new GeneralConfiguration(pulseLogger, plugin.getDataFolder(), "config.yml");
        this.platform = new BungeeCordPlatform(plugin);

        TaskScheduler scheduler = new BungeeCordTaskScheduler(plugin);

        this.databaseService = new DatabaseService(pulseLogger, platform, config, scheduler);

        this.diskRetriever = new DiskRetriever(plugin.getDataFolder());
        this.pingRetriever =new BungeeCordPingRetriever(plugin);

        ITPSRetriever tpsRetriever = new UnsupportedTPSRetriever();
        IMSPTRetriever msptRetriever = new UnsupportedMSPTRetriever();

        MetricsCollector collector = new MetricsCollector(pulseLogger, platform, tpsRetriever, diskRetriever, pingRetriever, msptRetriever);
        LineProtocolFormatter formatter = new LineProtocolFormatter(config);

        this.metricsService = new MetricsService(pulseLogger, collector, formatter, scheduler, databaseService);

        LOGGER.info("ServerPulse for BungeeCord initialized - waiting for server starting...");
    }

    @Override
    public void load() {
        LOGGER.info("Loading configuration...");
        config.load();

        databaseService.load();
        if (!platform.isEnabled()) {
            return;
        }
        metricsService.load();

        long intervalSeconds = config.getConfig().getLong("metrics.interval", 5);
        plugin.getProxy().getScheduler().schedule(plugin, metricsService::collectAndSendMetrics, 0, intervalSeconds, TimeUnit.SECONDS);

        // Register commands
        plugin.getProxy().getPluginManager().registerCommand(plugin, new ServerPulseCommand(config));
        ServerPulseProvider.register(new ServerPulseBungeeCordAPI(databaseService, metricsService, diskRetriever, pingRetriever));
    }

    @Override
    public void unload() {
        databaseService.unload();
        metricsService.unload();

        plugin.getProxy().getScheduler().cancel(plugin);
        ServerPulseProvider.unregister();
    }
}
