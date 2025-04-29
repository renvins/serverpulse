package it.renvins.serverpulse.bukkit.config;

import java.io.File;
import java.util.logging.Level;

import it.renvins.serverpulse.bukkit.ServerPulseBukkitLoader;
import it.renvins.serverpulse.bukkit.ServerPulseBukkit;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

public class BukkitConfiguration {

    private final ServerPulseBukkit plugin;
    private final String name;

    private File file;
    @Getter private YamlConfiguration config;

    /**
     * Constructs a new CustomConfig manager.
     *
     * @param plugin The main ServerPulsePlugin instance.
     * @param name   The name of the configuration file (e.g., "config.yml").
     */
    public BukkitConfiguration(ServerPulseBukkit plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    /**
     * Loads the configuration file. If the file doesn't exist,
     * it saves the default resource from the plugin JAR.
     */
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

    /**
     * Saves the current configuration state back to the file.
     * Logs an error if saving fails.
     */
    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            ServerPulseBukkitLoader.LOGGER.log(Level.SEVERE, "Could not save " + name + " file", e);
        }
    }

    /**
     * Reloads the configuration file, reloading the YamlConfiguration.
     * Logs an error if the file is null.
     *
     * @return true if the reload was successful, false otherwise.
     */
    public boolean reload() {
        if (file == null) {
            ServerPulseBukkitLoader.LOGGER.log(Level.SEVERE, "File is null, cannot reload configuration.");
            return false;
        }
        config = YamlConfiguration.loadConfiguration(file);
        return true;
    }

}
