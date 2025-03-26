package it.renvins.serverpulse;

import org.bukkit.plugin.java.JavaPlugin;

public class ServerPulsePlugin extends JavaPlugin {

    private final ServerPulseLoader loader = new ServerPulseLoader(this);

    @Override
    public void onEnable() {
        loader.load();
    }

    @Override
    public void onDisable() {
        loader.unload();
    }
}
