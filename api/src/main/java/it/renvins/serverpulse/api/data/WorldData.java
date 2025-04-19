package it.renvins.serverpulse.api.data;

public class WorldData {

    private final int entities;
    private final int loadedChunks;

    public WorldData(int entities, int loadedChunks) {
        this.entities = entities;
        this.loadedChunks = loadedChunks;
    }

    /**
     * Gets the number of entities in the world.
     *
     * @return the number of entities
     */
    public int getEntities() {
        return entities;
    }

    /**
     * Gets the number of loaded chunks in the world.
     *
     * @return the number of loaded chunks
     */
    public int getLoadedChunks() {
        return loadedChunks;
    }
}
