package it.renvins.serverpulse.fabric.platform;

import java.util.HashMap;
import java.util.Map;

import it.renvins.serverpulse.api.data.WorldData;
import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.fabric.ServerPulseFabric;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;

@RequiredArgsConstructor
public class FabricPlatform implements Platform {

    private final ServerPulseFabric mod;

    @Override
    public boolean isEnabled() {
        return mod.getServer() != null && FabricLoader.getInstance().isModLoaded("serverpulse");
    }

    @Override
    public boolean isPrimaryThread() {
        return mod.getServer().isOnThread();
    }

    @Override
    public int getOnlinePlayerCount() {
        return PlayerLookup.all(mod.getServer()).size();
    }

    @Override
    public Map<String, WorldData> getWorldsData() {
        Map<String, WorldData> worldsData = new HashMap<>();
        for (ServerWorld world : mod.getServer().getWorlds()) {
            WorldData worldData = new WorldData(
                    world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), entity -> true).size(),
                    world.getChunkManager().getLoadedChunkCount());

            worldsData.put(world.getRegistryKey().getValue().toString(), worldData);
        }
        return worldsData;
    }
}
