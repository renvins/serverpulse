package it.renvins.serverpulse.velocity;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(id = "serverpulse", name = "ServerPulse", version = "0.2.0-SNAPSHOT",
description = "Effortless Minecraft performance monitoring with pre-configured Grafana/InfluxDB via Docker.", authors = {"renvins"})
public class ServerPulseVelocity {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public ServerPulseVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization() {

    }
}
