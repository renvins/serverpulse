package it.renvins.serverpulse.service.impl;

import java.util.logging.Level;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import it.renvins.serverpulse.ServerPulseLoader;
import it.renvins.serverpulse.ServerPulsePlugin;
import it.renvins.serverpulse.config.CustomConfig;
import it.renvins.serverpulse.service.IDatabaseService;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

public class DatabaseService implements IDatabaseService {

    private final ServerPulsePlugin plugin;
    private final CustomConfig customConfig;

    @Getter private InfluxDBClient client;
    @Getter private WriteApi writeApi;

    public DatabaseService(ServerPulsePlugin plugin, CustomConfig customConfig) {
        this.plugin = plugin;
        this.customConfig = customConfig;
    }

    @Override
    public void load() {
        if (!checkConnectionData()) {
            ServerPulseLoader.LOGGER.severe("InfluxDB connection data is missing or wrong, shutting down the plugin...");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
        connect();
    }

    @Override
    public void unload() {
        if (writeApi != null) {
            writeApi.close();
            ServerPulseLoader.LOGGER.info("Write API closed successfully...");
        }
        if (client != null) {
            client.close();
            ServerPulseLoader.LOGGER.info("InfluxDB client closed successfully...");
        }
    }

    private void connect() {
        ConfigurationSection section = customConfig.getConfig().getConfigurationSection("metrics.influxdb");
        try {
            client = InfluxDBClientFactory.create(section.getString("url"), section.getString("token").toCharArray(),
                    section.getString("org"), section.getString("bucket"));
            writeApi = client.makeWriteApi();

            ServerPulseLoader.LOGGER.info("Connected successfully to InfluxDB :)");
        } catch (Exception e) {
            ServerPulseLoader.LOGGER.log(Level.SEVERE, "Could not connect to InfluxDB, shutting down the plugin...", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    private boolean checkConnectionData() {
        ConfigurationSection section = customConfig.getConfig().getConfigurationSection("metrics.influxdb");
        if (section == null) {
            return false;
        }
        String url = section.getString("url");
        String bucket = section.getString("bucket");
        String org = section.getString("org");
        String token = section.getString("token");

        return url != null && !url.isEmpty() && bucket != null && !bucket.isEmpty() &&
                org != null && !org.isEmpty() && token != null && !token.isEmpty();
    }
}
