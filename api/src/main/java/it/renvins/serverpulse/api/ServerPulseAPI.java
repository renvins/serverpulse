package it.renvins.serverpulse.api;

import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.service.IMetricsService;

public interface ServerPulseAPI {

    /**
     * Retrieves the instance of IDatabaseService.
     *
     * @return The IDatabaseService instance.
     */
    IDatabaseService getDatabaseService();

    /**
     * Retrieves the instance of IMetricsService.
     *
     * @return The IMetricsService instance.
     */
    IMetricsService getMetricsService();

    /**
     * Retrieves the instance of ITPSRetriever.
     *
     * @return The ITPSRetriever instance.
     * @throws UnsupportedOperationException if the API implementation does not support TPS retrieval.
     */
    default ITPSRetriever getTPSRetriever() {
        throw new UnsupportedOperationException("TPSRetriever is not supported in this API implementation.");
    }

    /**
     * Retrieves the instance of IDiskRetriever.
     *
     * @return The IDiskRetriever instance.
     */
    IDiskRetriever getDiskRetriever();


    /**
     * Retrieves the instance of IPingRetriever.
     *
     * @return The IPingRetriever instance.
     */
    IPingRetriever getPingRetriever();
}
