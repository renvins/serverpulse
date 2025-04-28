package it.renvins.serverpulse.paper;

import java.util.logging.Logger;

import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.api.service.Service;
import it.renvins.serverpulse.common.DatabaseService;
import it.renvins.serverpulse.common.config.DatabaseConfiguration;
import it.renvins.serverpulse.common.config.MetricsConfiguration;
import it.renvins.serverpulse.paper.commands.ServerPulseCommand;
import it.renvins.serverpulse.paper.config.PaperConfiguration;
import it.renvins.serverpulse.common.metrics.DiskRetriever;
import it.renvins.serverpulse.paper.config.PaperDatabaseConfiguration;
import it.renvins.serverpulse.paper.config.PaperMetricsConfiguration;
import it.renvins.serverpulse.paper.metrics.PingRetriever;
import it.renvins.serverpulse.paper.metrics.TPSRetriever;
import it.renvins.serverpulse.paper.platform.PaperPlatform;
import it.renvins.serverpulse.paper.scheduler.PaperTaskScheduler;
import it.renvins.serverpulse.common.MetricsService;

public class ServerPulsePaperLoader implements Service {

    private final ServerPulsePaper plugin;
    public static Logger LOGGER;

    private final PaperConfiguration config;

    private final PaperPlatform platform;
    private final PaperTaskScheduler taskScheduler;

    private final IDatabaseService databaseService;
    private final IMetricsService metricsService;

    private final ITPSRetriever tpsRetriever;
    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    public ServerPulsePaperLoader(ServerPulsePaper plugin) {
        this.plugin = plugin;
        LOGGER = plugin.getLogger();

        this.config = new PaperConfiguration(plugin, "config.yml");

        this.platform = new PaperPlatform(plugin);
        this.taskScheduler = new PaperTaskScheduler(plugin);

        DatabaseConfiguration databaseConfiguration = new PaperDatabaseConfiguration(config);
        MetricsConfiguration metricsConfiguration = new PaperMetricsConfiguration(config);

        this.databaseService = new DatabaseService(LOGGER, platform, databaseConfiguration, taskScheduler);
        this.metricsService = new MetricsService(LOGGER, platform, metricsConfiguration, taskScheduler);

        this.tpsRetriever = new TPSRetriever();
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

        plugin.getCommand("serverpulse").setExecutor(new ServerPulseCommand(config));
        ServerPulseProvider.register(new ServerPulsePaperAPI(databaseService, metricsService, tpsRetriever, diskRetriever, pingRetriever));
    }

    @Override
    public void unload() {
        databaseService.unload();
        metricsService.unload();

        ServerPulseProvider.unregister();
    }
}
