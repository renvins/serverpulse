package it.renvins.serverpulse.bungeecord;

import it.renvins.serverpulse.api.ServerPulseAPI;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerPulseBungeeCordAPI implements ServerPulseAPI {

    private final IDatabaseService databaseService;
    private final IMetricsService metricsService;
    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    @Override
    public IDatabaseService getDatabaseService() {
        return databaseService;
    }

    @Override
    public IMetricsService getMetricsService() {
        return metricsService;
    }

    @Override
    public IDiskRetriever getDiskRetriever() {
        return diskRetriever;
    }

    @Override
    public IPingRetriever getPingRetriever() {
        return pingRetriever;
    }
}
