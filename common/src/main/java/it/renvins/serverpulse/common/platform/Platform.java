package it.renvins.serverpulse.common.platform;

import java.util.Map;

import it.renvins.serverpulse.api.data.WorldData;

public interface Platform {

    /**
     * @return true if the plugin is enabled
     */
    boolean isEnabled();

    /**
     * Disable the plugin
     */
    void disable();

    /**
     * @return true if the current thread is the main thread of the server
     */
    boolean isPrimaryThread();

    /**
     * @return the number of online players
     */
    int getOnlinePlayerCount();

    /**
     * @return a map of world names to their data
     * @throws UnsupportedOperationException if the platform does not support this method
     */
    default Map<String, WorldData> getWorldsData() {
        throw new UnsupportedOperationException("This method is not supported on this platform.");
    }
}
