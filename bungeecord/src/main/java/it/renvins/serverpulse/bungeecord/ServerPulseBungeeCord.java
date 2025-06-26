package it.renvins.serverpulse.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;

public class ServerPulseBungeeCord extends Plugin {

    private final ServerPulseBungeeCordLoader loader = new ServerPulseBungeeCordLoader(this);

    @Override
    public void onEnable() {
        loader.load();
    }

    @Override
    public void onDisable() {
        loader.unload();
    }
}
