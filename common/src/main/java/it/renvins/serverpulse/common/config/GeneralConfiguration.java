package it.renvins.serverpulse.common.config;

import it.renvins.serverpulse.common.logger.PulseLogger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * General configuration class for managing the configuration file.
 * This class handles loading the configuration from a YAML file and creating a default configuration if it does not exist.
 */
@RequiredArgsConstructor
public class GeneralConfiguration {

    private final PulseLogger logger;

    private final File dataFolder;
    private final String name;

    @Getter private YamlFile config;

    /**
     * Loads the configuration file.
     * If the file does not exist, it creates a default configuration from the resources.
     *
     * @return true if the configuration was loaded successfully, false otherwise
     */
    public boolean load() {
        try {
            createDefaultConfigIfNotExists();
            config = new YamlFile(new File(dataFolder, name));

            config.load();
            logger.info("Successfully loaded configuration file: " + name);

            return true;
        } catch (IOException e) {
            logger.error("Failed to load configuration file: " + name, e);
            return false;
        }
    }

    /**
     * Creates the configuration file from the JAR's resources if it doesn't already exist.
     */
    private void createDefaultConfigIfNotExists() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile = new File(dataFolder, name);
        if (!configFile.exists()) {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(name)) {
                if (inputStream == null) {
                    logger.error("Failed to load default configuration file: " + name);
                    return;
                }
                Files.copy(inputStream, configFile.toPath());
                logger.info("Successfully created default configuration file: " + name);
            } catch (IOException e) {
                logger.error("Failed to copy default configuration file: " + name, e);
            }
        }
    }
}
