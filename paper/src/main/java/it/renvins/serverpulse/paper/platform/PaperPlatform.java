package it.renvins.serverpulse.paper.platform;

import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.paper.ServerPulsePaper;

public class PaperPlatform implements Platform {

    private final ServerPulsePaper plugin;

    public PaperPlatform(ServerPulsePaper plugin) {
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
