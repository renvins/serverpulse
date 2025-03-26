package it.renvins.serverpulse;

import java.util.logging.Logger;

import it.renvins.serverpulse.service.Service;

public class ServerPulseLoader implements Service {

    public static Logger LOGGER;

    public ServerPulseLoader(ServerPulsePlugin plugin) {
        LOGGER = plugin.getLogger();
    }

    @Override
    public void load() {
    }
}
