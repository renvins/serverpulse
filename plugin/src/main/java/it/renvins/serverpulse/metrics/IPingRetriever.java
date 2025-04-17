package it.renvins.serverpulse.metrics;

public interface IPingRetriever {

    /**
     * Retrieves the minimum ping of all online players.
     *
     * @return the minimum ping
     */
    int getMinPing();

    /**
     * Retrieves the maximum ping of all online players.
     *
     * @return the maximum ping
     */
    int getMaxPing();

    /**
     * Retrieves the average ping of all online players.
     *
     * @return the average ping
     */
    int getAveragePing();
}
