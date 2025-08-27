package it.renvins.serverpulse.velocity;

import java.nio.file.Path;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IMSPTRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;
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
import it.renvins.serverpulse.velocity.commands.ServerPulseCommand;
import it.renvins.serverpulse.velocity.logger.VelocityLogger;
import it.renvins.serverpulse.velocity.metrics.VelocityPingRetriever;
import it.renvins.serverpulse.velocity.platform.VelocityPlatform;
import it.renvins.serverpulse.velocity.scheduler.VelocityTaskScheduler;
import lombok.Getter;
import org.slf4j.Logger;

@Plugin(id = "serverpulse", name = "ServerPulse", version = "0.5.2-SNAPSHOT",
description = "Effortless Minecraft performance monitoring with pre-configured Grafana/InfluxDB via Docker.", authors = {"renvins"})
public class ServerPulseVelocity {

    @Getter private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private PulseLogger pulseLogger;

    private GeneralConfiguration config;

    private IDatabaseService databaseService;

    private IDiskRetriever diskRetriever;
    private IPingRetriever pingRetriever;

    private IMetricsService metricsService;

    @Inject
    public ServerPulseVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        logger.info("ServerPulse for Velocity initialized - waiting for proxy starting...");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.pulseLogger = new VelocityLogger(logger);
        this.config = new GeneralConfiguration(pulseLogger, dataDirectory.toFile(), "config.yml");

        logger.info("Loading configuration file...");
        config.load();

        Platform platform = new VelocityPlatform(this);
        TaskScheduler scheduler = new VelocityTaskScheduler(this);

        this.databaseService = new DatabaseService(pulseLogger, platform, config, scheduler);

        this.diskRetriever = new DiskRetriever(dataDirectory.toFile());
        this.pingRetriever = new VelocityPingRetriever(this);

        ITPSRetriever tpsRetriever = new UnsupportedTPSRetriever();
        IMSPTRetriever msptRetriever = new UnsupportedMSPTRetriever();

        MetricsCollector collector = new MetricsCollector(pulseLogger, platform, tpsRetriever, diskRetriever, pingRetriever, msptRetriever);
        LineProtocolFormatter formatter = new LineProtocolFormatter(config);

        this.metricsService = new MetricsService(pulseLogger, collector, formatter, scheduler, databaseService);

        databaseService.load();
        if (server.isShuttingDown()) {
            return;
        }
        metricsService.load();

        long intervalTicks = config.getConfig().getLong("metrics.interval", 5) * 20L;
        scheduler.runTaskTimerAsync(metricsService::collectAndSendMetrics, 0L, intervalTicks);

        CommandMeta meta = server.getCommandManager().metaBuilder("serverpulsevelocity")
                .plugin(this).aliases("spv").build();
        server.getCommandManager().register(meta, new ServerPulseCommand(config).createCommand());
        ServerPulseProvider.register(new ServerPulseVelocityAPI(databaseService, metricsService, diskRetriever, pingRetriever));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (databaseService != null) {
            databaseService.unload();
        }
        if (metricsService != null) {
            metricsService.unload();
        }

        try {
            ServerPulseProvider.unregister();
        } catch (IllegalStateException e) {
            // API might already be unregistered, that's okay
        }
        logger.info("ServerPulse for Velocity has been shut down.");
    }
}
