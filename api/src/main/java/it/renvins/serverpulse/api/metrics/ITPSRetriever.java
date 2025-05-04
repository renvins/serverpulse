package it.renvins.serverpulse.api.metrics;

public interface ITPSRetriever {

    /**
     * Retrieves the TPS (Ticks Per Second) of the server.
     *
     * @return an array of doubles representing the TPS values.
     *         The first value is the average TPS over the last minute,
     *         the second value is the average TPS over the last 5 minutes,
     *         and the third value is the average TPS over the last 15 minutes.
     */
     double[] getTPS();
}
