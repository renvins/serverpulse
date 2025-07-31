package it.renvins.serverpulse.fabric;

import java.util.logging.Logger;

import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IMSPTRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.common.DatabaseService;
import it.renvins.serverpulse.common.MetricsService;
import it.renvins.serverpulse.common.config.DatabaseConfiguration;
import it.renvins.serverpulse.common.config.GeneralConfiguration;
import it.renvins.serverpulse.common.config.MetricsConfiguration;
import it.renvins.serverpulse.common.logger.PulseLogger;
import it.renvins.serverpulse.common.disk.DiskRetriever;
import it.renvins.serverpulse.common.metrics.LineProtocolFormatter;
import it.renvins.serverpulse.common.metrics.MetricsCollector;
import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;
import it.renvins.serverpulse.fabric.command.ServerPulseCommand;
import it.renvins.serverpulse.fabric.logger.FabricLogger;
import it.renvins.serverpulse.fabric.metrics.FabricMSPTRetriever;
import it.renvins.serverpulse.fabric.metrics.FabricPingRetriever;
import it.renvins.serverpulse.fabric.metrics.FabricTPSRetriever;
import it.renvins.serverpulse.fabric.platform.FabricPlatform;
import it.renvins.serverpulse.fabric.task.FabricScheduler;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

public class ServerPulseFabric implements ModInitializer {

    public static final String MOD_ID = "serverpulse";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    private final GeneralConfiguration config;

    private final Platform platform;
    private final TaskScheduler scheduler;

    private final IDatabaseService databaseService;

    private final ITPSRetriever tpsRetriever;
    private final IMSPTRetriever msptRetriever;

    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    private final IMetricsService metricsService;

    public ServerPulseFabric() {
        PulseLogger logger = new FabricLogger();

        this.config = new GeneralConfiguration(logger, FabricLoader.getInstance().getConfigDir().resolve("serverpulse").toFile(), "config.yml");

        this.platform = new FabricPlatform(this);
        this.scheduler = new FabricScheduler();

        this.databaseService = new DatabaseService(logger, platform, config, scheduler);

        this.tpsRetriever = new FabricTPSRetriever();
        this.msptRetriever = new FabricMSPTRetriever();

        this.diskRetriever = new DiskRetriever(FabricLoader.getInstance().getGameDir().toFile());
        this.pingRetriever = new FabricPingRetriever(this);

        MetricsCollector collector = new MetricsCollector(logger, platform, tpsRetriever, diskRetriever, pingRetriever, msptRetriever);
        LineProtocolFormatter formatter = new LineProtocolFormatter(config);

        this.metricsService = new MetricsService(logger, collector, formatter, scheduler, databaseService);
    }

    @Getter private MinecraftServer server;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopped);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(new ServerPulseCommand(config).createCommand()));

        LOGGER.info("ServerPulse for Fabric initialized - waiting for server starting...");
    }

    private void onServerStarting(MinecraftServer server) {
        this.server = server;

        LOGGER.info("Loading configuration...");
        config.load();

        databaseService.load();
        if (!platform.isEnabled()) {
            return;
        }
        LOGGER.info("Starting tick monitoring task...");
        ((FabricTPSRetriever) tpsRetriever).startTickMonitor();
        ((FabricMSPTRetriever) msptRetriever).startMSPTMonitor();

        metricsService.load();

        long intervalSeconds = config.getConfig().getLong("metrics.interval", 5);
        scheduler.runTaskTimerAsync(metricsService::collectAndSendMetrics, 0, intervalSeconds);

        ServerPulseProvider.register(new ServerPulseFabricAPI(databaseService, metricsService, tpsRetriever, diskRetriever, pingRetriever));
    }

    private void onServerStopped(MinecraftServer server) {
        databaseService.unload();
        metricsService.unload();

        ServerPulseProvider.unregister();
        this.server = null;
    }
}
