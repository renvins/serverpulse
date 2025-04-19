package it.renvins.serverpulse;

import java.util.logging.Logger;

import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.api.service.Service;
import it.renvins.serverpulse.commands.ServerPulseCommand;
import it.renvins.serverpulse.config.CustomConfig;
import it.renvins.serverpulse.metrics.DiskRetriever;
import it.renvins.serverpulse.metrics.PingRetriever;
import it.renvins.serverpulse.service.DatabaseService;
import it.renvins.serverpulse.service.MetricsService;

public class ServerPulseLoader implements Service {

    private final ServerPulsePlugin plugin;
    public static Logger LOGGER;

    private final CustomConfig config;

    private final IDatabaseService databaseService;
    private final IMetricsService metricsService;

    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    public ServerPulseLoader(ServerPulsePlugin plugin) {
        this.plugin = plugin;
        LOGGER = plugin.getLogger();

        this.config = new CustomConfig(plugin, "config.yml");

        this.databaseService = new DatabaseService(plugin, config);
        this.metricsService = new MetricsService(plugin, config);

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
        ServerPulseProvider.register(new ServerPulsePaperAPI(databaseService, metricsService, diskRetriever, pingRetriever));
    }

    @Override
    public void unload() {
        databaseService.unload();
        metricsService.unload();

        ServerPulseProvider.register(null);
    }
}
