package it.renvins.serverpulse.paper;

import it.renvins.serverpulse.api.ServerPulseAPI;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;

public class ServerPulsePaperAPI implements ServerPulseAPI {

    private final IDatabaseService databaseService;
    private final IMetricsService metricsService;

    private final ITPSRetriever tpsRetriever;
    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    public ServerPulsePaperAPI(IDatabaseService databaseService, IMetricsService metricsService, ITPSRetriever tpsRetriever,
                               IDiskRetriever diskRetriever, IPingRetriever pingRetriever) {
        this.databaseService = databaseService;
        this.metricsService = metricsService;
        this.tpsRetriever = tpsRetriever;

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
    public ITPSRetriever getTPSRetriever() {
        return tpsRetriever;
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
