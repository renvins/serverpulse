package it.renvins.serverpulse.bungeecord.metrics;

import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.bungeecord.ServerPulseBungeeCord;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@RequiredArgsConstructor
public class BungeeCordPingRetriever implements IPingRetriever {

    private final ServerPulseBungeeCord plugin;

    @Override
    public int getMinPing() {
        int minPing = Integer.MAX_VALUE;
        if (plugin.getProxy().getPlayers().isEmpty()) {
            return 0;
        }
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            minPing = Math.min(minPing, player.getPing());
        }
        return minPing;
    }

    @Override
    public int getMaxPing() {
        int maxPing = 0;
        if (plugin.getProxy().getPlayers().isEmpty()) {
            return 0;
        }
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            maxPing = Math.max(maxPing, player.getPing());
        }
        return maxPing;
    }

    @Override
    public int getAveragePing() {
        int totalPing = 0;
        int playerCount = plugin.getProxy().getPlayers().size();
        if (playerCount == 0) {
            return 0;
        }
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            totalPing += player.getPing();
        }
        return totalPing / playerCount;
    }
}
