package it.renvins.serverpulse.bungeecord.config;

import it.renvins.serverpulse.bungeecord.ServerPulseBungeeCord;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class BungeeCordConfiguration {

    private final ServerPulseBungeeCord plugin;
    private final String name;

    private File file;
    @Getter private Configuration config;

    public BungeeCordConfiguration(ServerPulseBungeeCord plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        file = new File(plugin.getDataFolder(), name);

        if (!file.exists()) {
            // Copy default config from resources
            try (InputStream is = plugin.getResourceAsStream(name)) {
                is.transferTo(java.nio.file.Files.newOutputStream(file.toPath()));
                plugin.getLogger().info("Created default configuration file: " + name);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to copy default config", e);
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load config: " + name, e);
        }
    }

    public void save() {
        if (file == null || config == null) {
            return;
        }
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config!", e);
        }
    }

    public boolean reload() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to reload config: " + name, e);
            return false;
        }
    }
}
