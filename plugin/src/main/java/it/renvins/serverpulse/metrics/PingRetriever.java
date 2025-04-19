package it.renvins.serverpulse.metrics;

import it.renvins.serverpulse.api.metrics.IPingRetriever;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PingRetriever implements IPingRetriever {

    @Override
    public int getMinPing() {
        int minPing = Integer.MAX_VALUE;
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return 0;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            minPing = Math.min(minPing, player.getPing());
        }
        return minPing;
    }

    @Override
    public int getMaxPing() {
        int maxPing = 0;
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return 0;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            maxPing = Math.max(maxPing, player.getPing());
        }
        return maxPing;
    }

    @Override
    public int getAveragePing() {
        int totalPing = 0;
        int playerCount = Bukkit.getOnlinePlayers().size();
        if (playerCount == 0) {
            return 0;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            totalPing += player.getPing();
        }
        return totalPing / playerCount;
    }
}
