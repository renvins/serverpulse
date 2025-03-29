package it.renvins.serverpulse.service;

public interface IMetricsService extends Service {

    void sendMetrics();

    double getUsedHeap();
    double getAvailable();
}
