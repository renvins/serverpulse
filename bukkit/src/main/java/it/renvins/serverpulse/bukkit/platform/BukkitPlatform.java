package it.renvins.serverpulse.bukkit.platform;

import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.bukkit.ServerPulseBukkit;

public class BukkitPlatform implements Platform {

    private final ServerPulseBukkit plugin;

    public BukkitPlatform(ServerPulseBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public void disable() {
        plugin.getServer().getScheduler().cancelTasks(plugin);
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }
}
