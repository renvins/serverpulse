package it.renvins.serverpulse.bungeecord.logger;

import it.renvins.serverpulse.bungeecord.ServerPulseBungeeCord;
import it.renvins.serverpulse.common.logger.PulseLogger;
import lombok.RequiredArgsConstructor;

import java.util.logging.Level;

@RequiredArgsConstructor
public class BungeeCordLogger implements PulseLogger {

    private final ServerPulseBungeeCord plugin;

    @Override
    public void info(String message) {
        plugin.getLogger().info(message);
    }

    @Override
    public void warning(String message) {
        plugin.getLogger().warning(message);
    }

    @Override
    public void error(String message) {
        plugin.getLogger().log(Level.SEVERE, message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        plugin.getLogger().log(Level.SEVERE, message, throwable);
    }
}
