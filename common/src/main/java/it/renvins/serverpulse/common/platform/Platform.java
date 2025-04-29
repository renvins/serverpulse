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

    int getOnlinePlayerCount();
    Map<String, WorldData> getWorldsData();
}
