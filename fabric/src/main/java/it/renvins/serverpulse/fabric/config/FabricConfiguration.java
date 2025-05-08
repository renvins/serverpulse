package it.renvins.serverpulse.fabric.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

import it.renvins.serverpulse.fabric.ServerPulseFabric;
import lombok.Getter;
import org.simpleyaml.configuration.file.YamlFile;

public class FabricConfiguration {

    private final String name;

    @Getter private final YamlFile config;

    public FabricConfiguration(Path dataDir, String name) {
        this.name = name;

        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            ServerPulseFabric.LOGGER.log(Level.SEVERE,"Failed to create data directory: " + dataDir, e);
        }
        this.config = new YamlFile(new File(dataDir.toFile(), name));
    }

    public boolean load() {
        try {
            if (!config.exists()) {
                if (copyDefaultsFromResource()) {
                    ServerPulseFabric.LOGGER.info("Created configuration file: " + name);
                } else {
                    config.createNewFile();
                    ServerPulseFabric.LOGGER.info("Configuration file not found, created a new one: " + name);
                }
            } else {
                ServerPulseFabric.LOGGER.info("Loading configuration file: " + name);
            }
            config.load();
            return true;
        } catch (Exception e) {
            ServerPulseFabric.LOGGER.log(Level.SEVERE,"Failed to load configuration file: " + name, e);
            return false;
        }
    }

    private boolean copyDefaultsFromResource() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(name)) {
            if (in != null) {
                Files.copy(in, config.getConfigurationFile().toPath());
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            ServerPulseFabric.LOGGER.log(Level.SEVERE,"Failed to copy default configuration file: " + name, e);
            return false;
        }
    }
}
