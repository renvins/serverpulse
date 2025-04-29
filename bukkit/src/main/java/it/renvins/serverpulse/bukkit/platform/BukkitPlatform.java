package it.renvins.serverpulse.bukkit.platform;

import java.util.HashMap;
import java.util.Map;

import it.renvins.serverpulse.api.data.WorldData;
import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.bukkit.ServerPulseBukkit;
import org.bukkit.World;

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

    @Override
    public boolean isPrimaryThread() {
        return plugin.getServer().isPrimaryThread();
    }

    @Override
    public int getOnlinePlayerCount() {
        return plugin.getServer().getOnlinePlayers().size();
    }

    @Override
    public Map<String, WorldData> getWorldsData() {
        Map<String, WorldData> worldsData = new HashMap<>();
        for (World world : plugin.getServer().getWorlds()) {
            String name = world.getName();
            WorldData data = new WorldData(world.getEntities().size(), world.getLoadedChunks().length);

            worldsData.put(name, data);
        }
        return worldsData;
    }
}
