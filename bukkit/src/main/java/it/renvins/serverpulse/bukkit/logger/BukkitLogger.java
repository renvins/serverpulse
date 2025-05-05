package it.renvins.serverpulse.bukkit.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.renvins.serverpulse.common.logger.PulseLogger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BukkitLogger implements PulseLogger {

    private final Logger logger;

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warning(String message) {
        logger.warning(message);
    }

    @Override
    public void error(String message) {
        logger.severe(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }
}
