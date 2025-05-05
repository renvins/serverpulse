package it.renvins.serverpulse.velocity.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.Getter;
import org.simpleyaml.configuration.file.YamlFile;
import org.slf4j.Logger;

public class VelocityConfiguration {

    private final Logger logger;
    private final String name;

    @Getter private final YamlFile config;

    public VelocityConfiguration(Logger logger, Path dataDir, String name) {
        this.logger = logger;
        this.name = name;

        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            logger.error("Failed to create data directory: " + dataDir, e);
        }
        this.config = new YamlFile(new File(dataDir.toFile(), name));
    }

    public boolean load() {
        try {
            if (!config.exists()) {
                if (copyDefaultsFromResource()) {
                    logger.info("Created configuration file: " + name);
                } else {
                    config.createNewFile();
                    logger.info("Configuration file not found, created a new one: " + name);
                }
            } else {
                logger.info("Loading configuration file: " + name);
            }
            config.load();
            return true;
        } catch (Exception e) {
            logger.error("Failed to load configuration file: " + name, e);
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
            logger.error("Failed to copy default configuration file: " + name, e);
            return false;
        }
    }
}