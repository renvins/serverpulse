package it.renvins.serverpulse.bukkit;

import java.util.logging.Logger;

import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IMSPTRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.api.service.Service;
import it.renvins.serverpulse.bukkit.logger.BukkitLogger;
import it.renvins.serverpulse.bukkit.metrics.BukkitTPSRetriever;
import it.renvins.serverpulse.bukkit.metrics.PaperMSPTRetriever;
import it.renvins.serverpulse.common.DatabaseService;
import it.renvins.serverpulse.common.config.GeneralConfiguration;
import it.renvins.serverpulse.bukkit.commands.ServerPulseCommand;
import it.renvins.serverpulse.common.logger.PulseLogger;
import it.renvins.serverpulse.common.disk.DiskRetriever;
import it.renvins.serverpulse.bukkit.metrics.BukkitPingRetriever;
import it.renvins.serverpulse.bukkit.metrics.PaperTPSRetriever;
import it.renvins.serverpulse.bukkit.platform.BukkitPlatform;
import it.renvins.serverpulse.bukkit.scheduler.BukkitTaskScheduler;
import it.renvins.serverpulse.common.MetricsService;
import it.renvins.serverpulse.common.metrics.LineProtocolFormatter;
import it.renvins.serverpulse.common.metrics.MetricsCollector;
import it.renvins.serverpulse.common.metrics.UnsupportedMSPTRetriever;
import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;

public class ServerPulseBukkitLoader implements Service {

    private final ServerPulseBukkit plugin;
    public static Logger LOGGER;

    private final GeneralConfiguration config;

    private final Platform platform;

    private final IDatabaseService databaseService;

    private final ITPSRetriever tpsRetriever;
    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;
    private final IMSPTRetriever msptRetriever;

    private final IMetricsService metricsService;

    public ServerPulseBukkitLoader(ServerPulseBukkit plugin) {
        this.plugin = plugin;
        LOGGER = plugin.getLogger();

        PulseLogger logger = new BukkitLogger(LOGGER);

        this.config = new GeneralConfiguration(logger, plugin.getDataFolder(), "config.yml");

        this.platform = new BukkitPlatform(plugin);
        TaskScheduler taskScheduler = new BukkitTaskScheduler(plugin);

        this.databaseService = new DatabaseService(logger, platform, config, taskScheduler);

        if (isPaper()) {
            this.tpsRetriever = new PaperTPSRetriever();
            this.msptRetriever = new PaperMSPTRetriever();
        } else {
            this.tpsRetriever = new BukkitTPSRetriever(plugin);
            this.msptRetriever = new UnsupportedMSPTRetriever();
        }
        this.diskRetriever = new DiskRetriever(plugin.getDataFolder());
        this.pingRetriever = new BukkitPingRetriever();

        MetricsCollector collector = new MetricsCollector(logger, platform, tpsRetriever, diskRetriever, pingRetriever, msptRetriever);
        LineProtocolFormatter formatter = new LineProtocolFormatter(config);

        this.metricsService = new MetricsService(logger, collector, formatter, taskScheduler, databaseService);

        LOGGER.info("ServerPulse for Bukkit/Paper initialized - waiting for server starting...");
    }

    @Override
    public void load() {
        LOGGER.info("Loading configuration...");
        config.load();

        databaseService.load();
        if (!platform.isEnabled()) {
            return;
        }

        if (tpsRetriever instanceof BukkitTPSRetriever) {
            LOGGER.info("Starting tick monitoring task...");
            ((BukkitTPSRetriever) tpsRetriever).startTickMonitor();
        }
        if (msptRetriever instanceof PaperMSPTRetriever) {
            LOGGER.info("Registering PaperMSPTRetriever event listener...");
            plugin.getServer().getPluginManager().registerEvents((PaperMSPTRetriever) msptRetriever, plugin);
        }
        metricsService.load();

        long intervalTicks = config.getConfig().getLong("metrics.interval", 5) * 20L;
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, metricsService::collectAndSendMetrics, 0L, intervalTicks);

        plugin.getCommand("serverpulse").setExecutor(new ServerPulseCommand(config));
        ServerPulseProvider.register(new ServerPulseBukkitAPI(databaseService, metricsService, tpsRetriever, diskRetriever, pingRetriever));
    }

    @Override
    public void unload() {
        databaseService.unload();
        metricsService.unload();

        plugin.getServer().getScheduler().cancelTasks(plugin);
        ServerPulseProvider.unregister();
    }

    private boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            LOGGER.info("Server is running Paper (or a Paper fork). Using PaperTPSRetriever.");
            return true;
        } catch (ClassNotFoundException e) {
            LOGGER.info("Server is not running Paper (or a Paper fork). Using BukkitTPSRetriever.");
            return false;
        }
    }
}
