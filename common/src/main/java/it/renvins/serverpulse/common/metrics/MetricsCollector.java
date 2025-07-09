package it.renvins.serverpulse.common.metrics;

import it.renvins.serverpulse.api.data.AsyncMetricsSnapshot;
import it.renvins.serverpulse.api.data.SyncMetricsSnapshot;
import it.renvins.serverpulse.api.data.WorldData;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;
import it.renvins.serverpulse.api.metrics.IPingRetriever;
import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.api.utils.MemoryUtils;
import it.renvins.serverpulse.common.logger.PulseLogger;
import it.renvins.serverpulse.common.platform.Platform;

import java.util.Map;

/**
 * Collects raw metrics from the server.
 * This class is responsible for gathering data but does not know how it will be formatted or stored.
 * It depends on interfaces from the API module, making it platform-independent.
 */
public class MetricsCollector {

    private final PulseLogger logger;
    private final Platform platform;
    private final ITPSRetriever tpsRetriever;
    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    public MetricsCollector(PulseLogger logger, Platform platform, ITPSRetriever tpsRetriever, IDiskRetriever diskRetriever, IPingRetriever pingRetriever) {
        this.logger = logger;
        this.platform = platform;
        this.tpsRetriever = tpsRetriever;
        this.diskRetriever = diskRetriever;
        this.pingRetriever = pingRetriever;
    }

    /**
     * Collects metrics that must be gathered on the server's main thread.
     *
     * @return A SyncMetricsSnapshot containing the current synchronous server metrics.
     * @throws IllegalStateException if called from a non-primary thread.
     */
    public SyncMetricsSnapshot collectSyncSnapshot() {
        if (!platform.isPrimaryThread()) {
            logger.warning("Attempted to collect sync metrics from a non-primary thread.");
            throw new IllegalStateException("This method must be called on the main server thread.");
        }
        try {
            double[] tps = new double[]{0.0, 0.0, 0.0};
            Map<String, WorldData> worldsData = Map.of();
            int playerCount = platform.getOnlinePlayerCount();

            try {
                tps = this.tpsRetriever.getTPS();
                worldsData = platform.getWorldsData();
            } catch (UnsupportedOperationException ignored) {
            }

            return new SyncMetricsSnapshot(tps, playerCount, worldsData);
        } catch (Exception e) {
            logger.error("Unexpected error during sync data collection.", e);
            throw new RuntimeException("Sync data collection failed.", e);
        }
    }

    /**
     * Collects metrics that can be gathered from any thread.
     *
     * @return An AsyncMetricsSnapshot containing the current asynchronous server metrics.
     */
    public AsyncMetricsSnapshot collectAsyncSnapshot() {
        long usedHeap = MemoryUtils.getUsedHeapBytes();
        long committedHeap = MemoryUtils.getCommittedHeapBytes();

        long totalDisk = this.diskRetriever.getTotalSpace();
        long usableDisk = this.diskRetriever.getUsableSpace();

        int minPing = this.pingRetriever.getMinPing();
        int maxPing = this.pingRetriever.getMaxPing();
        int avgPing = this.pingRetriever.getAveragePing();

        return new AsyncMetricsSnapshot(usedHeap, committedHeap, totalDisk, usableDisk, minPing, maxPing, avgPing);
    }
}