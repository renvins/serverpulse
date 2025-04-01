package it.renvins.serverpulse.service;

public interface IMetricsService extends Service {

    /**
     * Collects and sends metrics to the configured InfluxDB instance.
     * This method is typically called periodically to gather and transmit
     * performance data.
     */
    void collectAndSendMetrics();
}
