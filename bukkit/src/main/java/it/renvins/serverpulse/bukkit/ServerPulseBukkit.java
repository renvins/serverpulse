package it.renvins.serverpulse.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class ServerPulseBukkit extends JavaPlugin {

    private final ServerPulseBukkitLoader loader = new ServerPulseBukkitLoader(this);

    @Override
    public void onEnable() {
        loader.load();
    }

    @Override
    public void onDisable() {
        loader.unload();
    }
}
