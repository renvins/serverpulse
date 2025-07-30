package it.renvins.serverpulse.api.metrics;

public interface IMSPTRetriever {

    /**
     * Retrieves the last tick duration in milliseconds.
     *
     * @return The last tick duration in milliseconds, or 0.0 if no ticks are recorded.
     */
    double getLastMSPT();

    /**
     * Retrieves the average tick duration over the last specified number of ticks.
     *
     * @param ticksCount The number of ticks to consider for the average.
     * @return The average tick duration in milliseconds, or 0.0 if no ticks are recorded or ticksCount is invalid.
     */
    double getAverageMSPT(int ticksCount);

    /**
     * Retrieves the minimum tick duration recorded.
     *
     * @return The minimum tick duration in milliseconds, or 0.0 if no ticks are recorded.
     */
    double getMinMSPT();

    /**
     * Retrieves the maximum tick duration recorded.
     *
     * @return The maximum tick duration in milliseconds, or 0.0 if no ticks are recorded.
     */
    double getMaxMSPT();
}
