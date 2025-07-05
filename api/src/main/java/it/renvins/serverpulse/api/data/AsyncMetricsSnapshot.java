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

    public long getUsedHeap() {
        return usedHeap;
    }

    public long getCommitedHeap() {
        return commitedHeap;
    }

    public long getTotalDisk() {
        return totalDisk;
    }

    public long getUsableDisk() {
        return usableDisk;
    }

    public long getMinPing() {
        return minPing;
    }

    public long getMaxPing() {
        return maxPing;
    }

    public long getAvgPing() {
        return avgPing;
    }
}
