package it.renvins.serverpulse.velocity.platform;

import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.velocity.ServerPulseVelocity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VelocityPlatform implements Platform {

    private final ServerPulseVelocity plugin;

    @Override
    public boolean isEnabled() {
        return !plugin.getServer().isShuttingDown();
    }

    @Override
    public void disable() {
        plugin.getServer().shutdown();
    }

    @Override
    public boolean isPrimaryThread() {
        return true;
    }

    @Override
    public int getOnlinePlayerCount() {
        return plugin.getServer().getAllPlayers().size();
    }
}
