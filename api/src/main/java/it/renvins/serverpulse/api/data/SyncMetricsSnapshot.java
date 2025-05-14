package it.renvins.serverpulse.api.data;

import java.util.Map;


public class SyncMetricsSnapshot {

    private final double[] tps;
    private final int playerCount;
    private final Map<String, WorldData> worldData;

    public SyncMetricsSnapshot(double[] tps, int playerCount, Map<String, WorldData> worldData) {
        this.tps = tps;
        this.playerCount = playerCount;
        this.worldData = worldData;
    }

    /**
     * Gets the ticks per second (TPS) for the server.
     *
     * @return an array of TPS values
     */
    public double[] getTps() {
        return tps;
    }

    /**
     * Gets the number of players currently online.
     *
     * @return the player count
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * Gets the world data for all worlds on the server.
     *
     * @return a map of world names to their respective WorldData objects
     */
    public Map<String, WorldData> getWorldData() {
        return worldData;
    }
}
