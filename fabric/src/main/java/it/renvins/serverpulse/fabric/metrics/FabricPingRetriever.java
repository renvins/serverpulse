package it.renvins.serverpulse.fabric.metrics;

import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.fabric.ServerPulseFabric;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.network.ServerPlayerEntity;

@RequiredArgsConstructor
public class FabricPingRetriever implements IPingRetriever {

    private final ServerPulseFabric mod;

    @Override
    public int getMinPing() {
        int minPing = Integer.MAX_VALUE;
        if (PlayerLookup.all(mod.getServer()).isEmpty()) {
            return 0;
        }
        for (ServerPlayerEntity player : PlayerLookup.all(mod.getServer())) {
            minPing = Math.toIntExact(Math.min(minPing, player.networkHandler.getLatency()));
        }
        return minPing;
    }

    @Override
    public int getMaxPing() {
        int maxPing = 0;
        if (PlayerLookup.all(mod.getServer()).isEmpty()) {
            return 0;
        }
        for (ServerPlayerEntity player : PlayerLookup.all(mod.getServer())) {
            maxPing = Math.toIntExact(Math.max(maxPing, player.networkHandler.getLatency()));
        }
        return maxPing;
    }

    @Override
    public int getAveragePing() {
        int totalPing = 0;
        int playerCount = PlayerLookup.all(mod.getServer()).size();
        if (playerCount == 0) {
            return 0;
        }
        for (ServerPlayerEntity player : PlayerLookup.all(mod.getServer())) {
            totalPing += player.networkHandler.getLatency();
        }
        return totalPing / playerCount;
    }
}
