package it.renvins.serverpulse.sponge;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("serverpulse")
public class ServerPulseSponge {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(StartedEngineEvent<Server> event) {

    }
}
