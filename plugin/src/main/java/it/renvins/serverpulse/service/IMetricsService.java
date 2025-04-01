package it.renvins.serverpulse.service;

public interface IMetricsService extends Service {

    /**
     * Collects the current server metrics and sends them to the database.
     */
    void sendMetrics();
}
