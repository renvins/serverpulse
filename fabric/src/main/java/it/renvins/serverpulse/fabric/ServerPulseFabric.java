package it.renvins.serverpulse.fabric;

import java.util.logging.Logger;

import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.metrics.ITPSRetriever;
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
import it.renvins.serverpulse.fabric.config.FabricConfiguration;
import it.renvins.serverpulse.fabric.config.FabricDatabaseConfiguration;
import it.renvins.serverpulse.fabric.config.FabricMetricsConfiguration;
import it.renvins.serverpulse.fabric.logger.FabricLogger;
import it.renvins.serverpulse.fabric.metrics.FabricPingRetriever;
import it.renvins.serverpulse.fabric.metrics.FabricTPSRetriever;
import it.renvins.serverpulse.fabric.platform.FabricPlatform;
import it.renvins.serverpulse.fabric.task.FabricScheduler;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

public class ServerPulseFabric implements ModInitializer {

    public static final String MOD_ID = "serverpulse";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    private final FabricConfiguration config;

    private final Platform platform;

    private final IDatabaseService databaseService;
    private final IMetricsService metricsService;

    private final ITPSRetriever tpsRetriever;
    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    public ServerPulseFabric() {
        this.config = new FabricConfiguration(FabricLoader.getInstance().getConfigDir().resolve("serverpulse"), "config.yml");

        PulseLogger logger = new FabricLogger();

        this.platform = new FabricPlatform(this);
        TaskScheduler scheduler = new FabricScheduler();

        DatabaseConfiguration dbConfig = new FabricDatabaseConfiguration(config);
        MetricsConfiguration metricsConfig = new FabricMetricsConfiguration(config);

        this.databaseService = new DatabaseService(logger, platform, dbConfig, scheduler);
        this.metricsService = new MetricsService(logger, platform, metricsConfig, scheduler);

        this.tpsRetriever = new FabricTPSRetriever();
        this.diskRetriever = new DiskRetriever(FabricLoader.getInstance().getGameDir().toFile());
        this.pingRetriever = new FabricPingRetriever(this);
    }

    @Getter private MinecraftServer server;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopped);

        LOGGER.info("ServerPulse for Fabric initialized - waiting for server starting...");
    }

    private void onServerStarting(MinecraftServer server) {
        LOGGER.info("ServerPulse is starting...");
        this.server = server;

        config.load();

        ServerPulseProvider.register(new ServerPulseFabricAPI(databaseService, metricsService, tpsRetriever, diskRetriever, pingRetriever));

        databaseService.load();
        if (!platform.isEnabled()) {
            return;
        }
        metricsService.load();

        LOGGER.info("Starting tick monitoring task...");
        ((FabricTPSRetriever) tpsRetriever).startTickMonitor();
    }

    private void onServerStopped(MinecraftServer server) {
        LOGGER.info("ServerPulse is stopping...");

        databaseService.unload();
        metricsService.unload();

        ServerPulseProvider.unregister();
        this.server = null;
    }
}
