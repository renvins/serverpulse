package it.renvins.serverpulse.fabric.logger;

import java.util.logging.Level;

import it.renvins.serverpulse.common.logger.PulseLogger;
import it.renvins.serverpulse.fabric.ServerPulseFabric;

public class FabricLogger implements PulseLogger {

    @Override
    public void info(String message) {
        ServerPulseFabric.LOGGER.info(message);
    }

    @Override
    public void warning(String message) {
        ServerPulseFabric.LOGGER.warning(message);
    }

    @Override
    public void error(String message) {
        ServerPulseFabric.LOGGER.severe(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        ServerPulseFabric.LOGGER.log(Level.SEVERE, message, throwable);
    }
}
