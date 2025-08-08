package it.renvins.serverpulse.api.data;


public class AsyncMetricsSnapshot {

    private final long usedHeap;
    private final long commitedHeap;

    private final long totalDisk;
    private final long usableDisk;

    private final long minPing;
    private final long maxPing;
    private final long avgPing;

    private final double mspt1m;
    private final double mspt5m;
    private final double mspt15m;

    private final double lastMSPT;
    private final double minMSPT;
    private final double maxMSPT;

    private final double systemCpuLoadRatio;
    private final double processCpuLoadRatio;
    private final int availableProcessors;


    public AsyncMetricsSnapshot(long usedHeap, long commitedHeap,
                                long totalDisk, long usableDisk,
                                long minPing, long maxPing, long avgPing,
                                double mspt1m, double mspt5m, double mspt15m,
                                double lastMSPT, double minMSPT, double maxMSPT,
                                double systemCpuLoadRatio, double processCpuLoadRatio, int availableProcessors) {
        this.usedHeap = usedHeap;
        this.commitedHeap = commitedHeap;

        this.totalDisk = totalDisk;
        this.usableDisk = usableDisk;

        this.minPing = minPing;
        this.maxPing = maxPing;
        this.avgPing = avgPing;

        this.mspt1m = mspt1m;
        this.mspt5m = mspt5m;
        this.mspt15m = mspt15m;

        this.lastMSPT = lastMSPT;
        this.minMSPT = minMSPT;
        this.maxMSPT = maxMSPT;

        this.systemCpuLoadRatio = systemCpuLoadRatio;
        this.processCpuLoadRatio = processCpuLoadRatio;
        this.availableProcessors = availableProcessors;
    }

    /**
     * Gets the amount of used heap memory in bytes.
     *
     * @return the used heap memory
     */
    public long getUsedHeap() {
        return usedHeap;
    }

    /**
     * Gets the amount of committed heap memory in bytes.
     *
     * @return the committed heap memory
     */
    public long getCommitedHeap() {
        return commitedHeap;
    }

    /**
     * Gets the total disk space available in bytes.
     *
     * @return the total disk space
     */
    public long getTotalDisk() {
        return totalDisk;
    }

    /**
     * Gets the usable disk space available in bytes.
     *
     * @return the usable disk space
     */
    public long getUsableDisk() {
        return usableDisk;
    }

    /**
     * Gets the minimum ping recorded in milliseconds.
     *
     * @return the minimum ping
     */
    public long getMinPing() {
        return minPing;
    }

    /**
     * Gets the maximum ping recorded in milliseconds.
     *
     * @return the maximum ping
     */
    public long getMaxPing() {
        return maxPing;
    }

    /**
     * Gets the average ping recorded in milliseconds.
     *
     * @return the average ping
     */
    public long getAvgPing() {
        return avgPing;
    }

    /**
     * Gets the average server tick duration over the last 1 minute in milliseconds.
     *
     * @return the average tick duration for the last 1 minute
     */
    public double getMspt1m() {
        return mspt1m;
    }

    /**
     * Gets the average server tick duration over the last 5 minutes in milliseconds.
     *
     * @return the average tick duration for the last 5 minutes
     */
    public double getMspt5m() {
        return mspt5m;
    }

    /**
     * Gets the average server tick duration over the last 15 minutes in milliseconds.
     *
     * @return the average tick duration for the last 15 minutes
     */
    public double getMspt15m() {
        return mspt15m;
    }

    /**
     * Gets the last server tick duration in milliseconds.
     *
     * @return the last tick duration
     */
    public double getLastMSPT() {
        return lastMSPT;
    }

    /**
     * Gets the minimum server tick duration recorded in milliseconds.
     *
     * @return the minimum tick duration
     */
    public double getMinMSPT() {
        return minMSPT;
    }

    /**
     * Gets the maximum server tick duration recorded in milliseconds.
     *
     * @return the maximum tick duration
     */
    public double getMaxMSPT() {
        return maxMSPT;
    }

    /**
     * Gets the system-wide CPU load ratio (0.0 to 1.0)
     * @return the system CPU load ratio
     */
    public double getSystemCpuLoadRatio() {
        return systemCpuLoadRatio;
    }

    /** 
     * Gets the JVM process CPU load ratio (0.0 to 1.0)
     * @return the process CPU load ratio
     */
    public double getProcessCpuLoadRatio() {
        return processCpuLoadRatio;
    }

    /**
     * Gets the number of available processors
     * @return the number of available processors
     */
    public int getAvailableProcessors() {
        return availableProcessors;
    }
}
