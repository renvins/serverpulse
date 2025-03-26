package it.renvins.serverpulse.config;

import java.io.File;
import java.util.logging.Level;

import it.renvins.serverpulse.ServerPulseLoader;
import it.renvins.serverpulse.ServerPulsePlugin;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfig {

    private final String name;
    private final File file;
    @Getter private YamlConfiguration config;

    public CustomConfig(ServerPulsePlugin plugin, String name) {
        this.name = name;

        this.file = new File(plugin.getDataFolder().getAbsolutePath() + "/" + name);
    }

    public void load() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                ServerPulseLoader.LOGGER.log(Level.SEVERE, "Could not create " + name + " file", e);
            }
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
