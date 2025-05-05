package it.renvins.serverpulse.velocity.metrics;

import com.velocitypowered.api.proxy.Player;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.velocity.ServerPulseVelocity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VelocityPingRetriever implements IPingRetriever {

    private final ServerPulseVelocity plugin;

    @Override
    public int getMinPing() {
        int minPing = Integer.MAX_VALUE;
        if (plugin.getServer().getAllPlayers().isEmpty()) {
            return 0;
        }
        for (Player player : plugin.getServer().getAllPlayers()) {
            minPing = Math.toIntExact(Math.min(minPing, player.getPing()));
        }
        return minPing;
    }

    @Override
    public int getMaxPing() {
        int maxPing = 0;
        if (plugin.getServer().getAllPlayers().isEmpty()) {
            return 0;
        }
        for (Player player : plugin.getServer().getAllPlayers()) {
            maxPing = Math.toIntExact(Math.max(maxPing, player.getPing()));
        }
        return maxPing;
    }

    @Override
    public int getAveragePing() {
        int totalPing = 0;
        int playerCount = plugin.getServer().getAllPlayers().size();
        if (playerCount == 0) {
            return 0;
        }
        for (Player player : plugin.getServer().getAllPlayers()) {
            totalPing += (int) player.getPing();
        }
        return totalPing / playerCount;
    }
}

