package it.renvins.serverpulse.bukkit;

import java.util.logging.Logger;

import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.api.service.Service;
import it.renvins.serverpulse.bukkit.metrics.BukkitTPSRetriever;
import it.renvins.serverpulse.common.DatabaseService;
import it.renvins.serverpulse.common.config.DatabaseConfiguration;
import it.renvins.serverpulse.common.config.MetricsConfiguration;
import it.renvins.serverpulse.bukkit.commands.ServerPulseCommand;
import it.renvins.serverpulse.bukkit.config.BukkitConfiguration;
import it.renvins.serverpulse.common.metrics.DiskRetriever;
import it.renvins.serverpulse.bukkit.config.BukkitDatabaseConfiguration;
import it.renvins.serverpulse.bukkit.config.BukkitMetricsConfiguration;
import it.renvins.serverpulse.bukkit.metrics.PingRetriever;
import it.renvins.serverpulse.bukkit.metrics.PaperTPSRetriever;
import it.renvins.serverpulse.bukkit.platform.BukkitPlatform;
import it.renvins.serverpulse.bukkit.scheduler.BukkitTaskScheduler;
import it.renvins.serverpulse.common.MetricsService;

public class ServerPulseBukkitLoader implements Service {

    private final ServerPulseBukkit plugin;
    public static Logger LOGGER;

    private final BukkitConfiguration config;

    private final BukkitPlatform platform;
    private final BukkitTaskScheduler taskScheduler;

    private final IDatabaseService databaseService;
    private final IMetricsService metricsService;

    private final ITPSRetriever tpsRetriever;
    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    public ServerPulseBukkitLoader(ServerPulseBukkit plugin) {
        this.plugin = plugin;
        LOGGER = plugin.getLogger();

        this.config = new BukkitConfiguration(plugin, "config.yml");

        this.platform = new BukkitPlatform(plugin);
        this.taskScheduler = new BukkitTaskScheduler(plugin);

        DatabaseConfiguration databaseConfiguration = new BukkitDatabaseConfiguration(config);
        MetricsConfiguration metricsConfiguration = new BukkitMetricsConfiguration(config);

        this.databaseService = new DatabaseService(LOGGER, platform, databaseConfiguration, taskScheduler);
        this.metricsService = new MetricsService(LOGGER, platform, metricsConfiguration, taskScheduler);

        if (isPaper()) {
            this.tpsRetriever = new PaperTPSRetriever();
        } else {
            this.tpsRetriever = new BukkitTPSRetriever(plugin);
        }
        this.diskRetriever = new DiskRetriever(plugin.getDataFolder());
        this.pingRetriever = new PingRetriever();
    }

    @Override
    public void load() {
        LOGGER.info("Loading configuration...");
        config.load();

        if(!config.getConfig().getBoolean("metrics.enabled")) {
            LOGGER.severe("Shutting down the plugin because metrics are disabled!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);

            return;
        }
        databaseService.load();
        if (!plugin.isEnabled()) {
            return;
        }
        metricsService.load();

        if (tpsRetriever instanceof BukkitTPSRetriever) {
            ((BukkitTPSRetriever) tpsRetriever).startTickMonitor();
        }

        plugin.getCommand("serverpulse").setExecutor(new ServerPulseCommand(config));
        ServerPulseProvider.register(new ServerPulseBukkitAPI(databaseService, metricsService, tpsRetriever, diskRetriever, pingRetriever));
    }

    @Override
    public void unload() {
        databaseService.unload();
        metricsService.unload();

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
