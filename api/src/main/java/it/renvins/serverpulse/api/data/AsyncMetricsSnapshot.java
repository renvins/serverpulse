package it.renvins.serverpulse.api.data;


public class AsyncMetricsSnapshot {

    private final long usedHeap;
    private final long commitedHeap;

    private final long totalDisk;
    private final long usableDisk;

    private final long minPing;
    private final long maxPing;
    private final long avgPing;


    public AsyncMetricsSnapshot(long usedHeap, long commitedHeap, long totalDisk, long usableDisk, long minPing, long maxPing, long avgPing) {
        this.usedHeap = usedHeap;
        this.commitedHeap = commitedHeap;

        this.totalDisk = totalDisk;
        this.usableDisk = usableDisk;

        this.minPing = minPing;
        this.maxPing = maxPing;
        this.avgPing = avgPing;
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
}
