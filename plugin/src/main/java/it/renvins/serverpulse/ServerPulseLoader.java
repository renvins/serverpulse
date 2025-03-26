package it.renvins.serverpulse;

import java.util.logging.Logger;

import it.renvins.serverpulse.config.CustomConfig;
import it.renvins.serverpulse.service.IDatabaseService;
import it.renvins.serverpulse.service.Service;
import it.renvins.serverpulse.service.impl.DatabaseService;

public class ServerPulseLoader implements Service {

    private final ServerPulsePlugin plugin;
    public static Logger LOGGER;

    private final CustomConfig config;

    private final IDatabaseService databaseService;

    public ServerPulseLoader(ServerPulsePlugin plugin) {
        this.plugin = plugin;
        LOGGER = plugin.getLogger();

        this.config = new CustomConfig(plugin, "config.yml");
        this.databaseService = new DatabaseService(plugin, config);
    }

    @Override
    public void load() {
        LOGGER.info("Loading metrics' configuration...");
        config.load();

        if(!config.getConfig().getBoolean("metrics.enabled")) {
            LOGGER.severe("Shutting down the plugin because metrics are disabled!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);

            return;
        }
        databaseService.load();
    }
}
