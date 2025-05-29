package it.renvins.serverpulse.bungeecord.platform;

import it.renvins.serverpulse.bungeecord.ServerPulseBungeeCord;
import it.renvins.serverpulse.common.platform.Platform;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BungeeCordPlatform implements Platform {

    private final ServerPulseBungeeCord plugin;

    // Needs to be done in this horrible way here in Bungee
    @Override
    public boolean isEnabled() {
        return plugin != null &&
                plugin.getProxy().getPluginManager().getPlugin(plugin.getDescription().getName()) != null;
    }

    @Override
    public void disable() {
        plugin.getProxy().stop();
    }

    @Override
    public boolean isPrimaryThread() {
        return true;
    }

    @Override
    public int getOnlinePlayerCount() {
        return plugin.getProxy().getOnlineCount();
    }
}
