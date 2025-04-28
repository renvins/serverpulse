package it.renvins.serverpulse.paper;

import java.util.logging.Logger;

import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.api.service.Service;
import it.renvins.serverpulse.common.DatabaseService;
import it.renvins.serverpulse.common.config.DatabaseConfiguration;
import it.renvins.serverpulse.paper.commands.ServerPulseCommand;
import it.renvins.serverpulse.paper.config.PaperConfiguration;
import it.renvins.serverpulse.common.metrics.DiskRetriever;
import it.renvins.serverpulse.paper.metrics.PingRetriever;
import it.renvins.serverpulse.paper.platform.PaperPlatform;
import it.renvins.serverpulse.paper.scheduler.PaperTaskScheduler;
import it.renvins.serverpulse.paper.service.MetricsService;

public class ServerPulsePaperLoader implements Service {

    private final ServerPulsePaper plugin;
    public static Logger LOGGER;

    private final PaperConfiguration config;

    private final IDatabaseService databaseService;
    private final IMetricsService metricsService;

    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    public ServerPulsePaperLoader(ServerPulsePaper plugin) {
        this.plugin = plugin;
        LOGGER = plugin.getLogger();

        this.config = new PaperConfiguration(plugin, "config.yml");
        LOGGER.info("Loading configuration...");
        config.load();

        DatabaseConfiguration db = new DatabaseConfiguration(
                config.getConfig().getString("metrics.influxdb.url"),
                config.getConfig().getString("metrics.influxdb.bucket"),
                config.getConfig().getString("metrics.influxdb.org"),
                config.getConfig().getString("metrics.influxdb.token"));

        this.databaseService = new DatabaseService(LOGGER, db, new PaperPlatform(plugin), new PaperTaskScheduler(plugin));
        this.metricsService = new MetricsService(plugin, config);

        this.diskRetriever = new DiskRetriever(plugin.getDataFolder());
        this.pingRetriever = new PingRetriever();
    }

    @Override
    public void load() {
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
        ServerPulseProvider.register(new ServerPulsePaperAPI(databaseService, metricsService, diskRetriever, pingRetriever));
    }

    @Override
    public void unload() {
        databaseService.unload();
        metricsService.unload();

        ServerPulseProvider.unregister();
    }
}
