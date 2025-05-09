package it.renvins.serverpulse.velocity;

import java.nio.file.Path;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.common.DatabaseService;
import it.renvins.serverpulse.common.MetricsService;
import it.renvins.serverpulse.common.config.DatabaseConfiguration;
import it.renvins.serverpulse.common.config.MetricsConfiguration;
import it.renvins.serverpulse.common.logger.PulseLogger;
import it.renvins.serverpulse.common.metrics.DiskRetriever;
import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;
import it.renvins.serverpulse.velocity.commands.ServerPulseCommand;
import it.renvins.serverpulse.velocity.config.VelocityConfiguration;
import it.renvins.serverpulse.velocity.config.VelocityDatabaseConfiguration;
import it.renvins.serverpulse.velocity.config.VelocityMetricsConfiguration;
import it.renvins.serverpulse.velocity.logger.VelocityLogger;
import it.renvins.serverpulse.velocity.metrics.VelocityPingRetriever;
import it.renvins.serverpulse.velocity.platform.VelocityPlatform;
import it.renvins.serverpulse.velocity.scheduler.VelocityTaskScheduler;
import lombok.Getter;
import org.slf4j.Logger;

@Plugin(id = "serverpulse", name = "ServerPulse", version = "0.2.0-SNAPSHOT",
description = "Effortless Minecraft performance monitoring with pre-configured Grafana/InfluxDB via Docker.", authors = {"renvins"})
public class ServerPulseVelocity {

    @Getter private final ProxyServer server;
    private final PulseLogger logger;

    private final VelocityConfiguration config;

    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    private IDatabaseService databaseService;
    private IMetricsService metricsService;

    @Inject
    public ServerPulseVelocity(ProxyServer server, @DataDirectory Path dataDirectory, Logger logger) {
        this.server = server;

        this.logger = new VelocityLogger(logger);
        this.config = new VelocityConfiguration(logger, dataDirectory, "config.yml");

        this.diskRetriever = new DiskRetriever(dataDirectory.toFile());
        this.pingRetriever = new VelocityPingRetriever(this);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Loading configuration file...");
        config.load();

        if(!config.getConfig().getBoolean("metrics.enabled")) {
            logger.error("Shutting down the plugin because metrics are disabled!");
            server.shutdown();

            return;
        }
        DatabaseConfiguration dbConfig = new VelocityDatabaseConfiguration(config);
        MetricsConfiguration metricsConfig = new VelocityMetricsConfiguration(config);

        Platform platform = new VelocityPlatform(this);
        TaskScheduler scheduler = new VelocityTaskScheduler(this);

        this.databaseService = new DatabaseService(logger, platform, dbConfig, scheduler);
        this.metricsService = new MetricsService(logger, platform, metricsConfig, scheduler);

        ServerPulseProvider.register(new ServerPulseVelocityAPI(databaseService, metricsService, diskRetriever, pingRetriever));

        databaseService.load();
        if (server.isShuttingDown()) {
            return;
        }

        metricsService.load();

        CommandMeta meta = server.getCommandManager().metaBuilder("serverpulsevelocity")
                .plugin(this).aliases("spv").build();
        server.getCommandManager().register(meta, new ServerPulseCommand(config).createCommand());
    }
}
