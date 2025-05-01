package it.renvins.serverpulse.velocity;

import it.renvins.serverpulse.api.ServerPulseAPI;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;

public class ServerPulseVelocityAPI implements ServerPulseAPI {

    @Override
    public IDatabaseService getDatabaseService() {
        return null;
    }

    @Override
    public IMetricsService getMetricsService() {
        return null;
    }

    @Override
    public ITPSRetriever getTPSRetriever() {
        return null;
    }

    @Override
    public IDiskRetriever getDiskRetriever() {
        return null;
    }

    @Override
    public IPingRetriever getPingRetriever() {
        return null;
    }
}
