package it.renvins.serverpulse.api.service;

public interface Service {

    /**
     * Loads the service. This method is typically called during the
     * initialization phase of the application to set up the service.
     */
    void load();

    /**
     * Unloads the service. This method is typically called during the
     * shutdown phase of the application to clean up resources and stop
     * any ongoing processes.
     */
    default void unload() {}
}
