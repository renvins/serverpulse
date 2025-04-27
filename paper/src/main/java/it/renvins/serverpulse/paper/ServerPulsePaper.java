package it.renvins.serverpulse.paper;

import org.bukkit.plugin.java.JavaPlugin;

public class ServerPulsePaper extends JavaPlugin {

    private final ServerPulsePaperLoader loader = new ServerPulsePaperLoader(this);

    @Override
    public void onEnable() {
        loader.load();
    }

    @Override
    public void onDisable() {
        loader.unload();
    }
}
