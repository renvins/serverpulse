package it.renvins.serverpulse.velocity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.common.DatabaseService;
import it.renvins.serverpulse.common.MetricsService;
import it.renvins.serverpulse.common.config.DatabaseConfiguration;
import lombok.Getter;
import org.slf4j.Logger;

@Plugin(id = "serverpulse", name = "ServerPulse", version = "0.2.0-SNAPSHOT",
description = "Effortless Minecraft performance monitoring with pre-configured Grafana/InfluxDB via Docker.", authors = {"renvins"})
public class ServerPulseVelocity {

    @Getter private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private IDatabaseService databaseService;
    private IMetricsService metricsService;

    @Inject
    public ServerPulseVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization() {
        this.databaseService = new DatabaseService();
        this.metricsService = new MetricsService();
    }

    private void loadConfig() {
    }
}
