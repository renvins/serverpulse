package it.renvins.serverpulse;

import it.renvins.serverpulse.api.ServerPulseAPI;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;

public class ServerPulsePaperAPI implements ServerPulseAPI {

    private final IDatabaseService databaseService;
    private final IMetricsService metricsService;

    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    public ServerPulsePaperAPI(IDatabaseService databaseService, IMetricsService metricsService,
                               IDiskRetriever diskRetriever, IPingRetriever pingRetriever) {
        this.databaseService = databaseService;
        this.metricsService = metricsService;

        this.diskRetriever = diskRetriever;
        this.pingRetriever = pingRetriever;
    }

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
