package it.renvins.serverpulse.fabric;

import java.util.logging.Logger;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class ServerPulseFabric implements ModInitializer {

    public static final String MOD_ID = "serverpulse";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    @Getter private MinecraftServer server;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopped);

        LOGGER.info("ServerPulse for Fabric initialized - waiting for server starting...");
    }

    private void onServerStarting(MinecraftServer server) {
        LOGGER.info("ServerPulse is starting...");
        this.server = server;
    }

    private void onServerStopped(MinecraftServer server) {
        LOGGER.info("ServerPulse is stopping...");
        this.server = null;
    }
}
