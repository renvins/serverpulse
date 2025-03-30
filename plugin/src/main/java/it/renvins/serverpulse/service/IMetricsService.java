package it.renvins.serverpulse.service;

public interface IMetricsService extends Service {

    /**
     * Collects the current server metrics and sends them to the database.
     */
    void sendMetrics();

    /**
     * Gets the currently used heap memory in megabytes (MB).
     *
     * @return Used heap memory in MB.
     */
    double getUsedHeap();

    /**
     * Gets the currently available heap memory in megabytes (MB).
     * This is calculated as Max Heap - Used Heap.
     * Returns -1.0 if the maximum heap size cannot be determined.
     *
     * @return Available heap memory in MB, or -1.0 if max heap is indeterminate.
     */
    double getAvailable();
}
