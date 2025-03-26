package it.renvins.serverpulse.config;

import java.io.File;
import java.util.logging.Level;

import it.renvins.serverpulse.ServerPulseLoader;
import it.renvins.serverpulse.ServerPulsePlugin;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfig {

    private final ServerPulsePlugin plugin;
    private final String name;

    private File file;
    @Getter private YamlConfiguration config;

    public CustomConfig(ServerPulsePlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            ServerPulseLoader.LOGGER.log(Level.SEVERE, "Could not save " + name + " file", e);
        }
    }

}
