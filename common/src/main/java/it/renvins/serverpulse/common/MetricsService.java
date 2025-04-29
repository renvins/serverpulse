package it.renvins.serverpulse.common;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.utils.MemoryUtils;
import it.renvins.serverpulse.api.data.SyncMetricsSnapshot;
import it.renvins.serverpulse.api.data.WorldData;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.common.config.MetricsConfiguration;
import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;

public class MetricsService implements IMetricsService {

    private final Logger logger;

    private final Platform platform;
    private final MetricsConfiguration configuration;

    private final TaskScheduler scheduler;
    private final Executor asyncExecutor;

    public MetricsService(Logger logger, Platform platform, MetricsConfiguration configuration, TaskScheduler scheduler) {
        this.logger = logger;

        this.platform = platform;
        this.configuration = configuration;

        this.scheduler = scheduler;
        this.asyncExecutor = scheduler::runAsync;
    }

    @Override
    public void load() {
        logger.info("Loading metrics task...");
        loadTask();
    }

    @Override
    public void collectAndSendMetrics() {
        if (!ServerPulseProvider.get().getDatabaseService().isConnected() || ServerPulseProvider.get().getDatabaseService().getWriteApi() == null) {
            return;
        }
        if (!ServerPulseProvider.get().getDatabaseService().ping()) {
            ServerPulseProvider.get().getDatabaseService().disconnect();
            ServerPulseProvider.get().getDatabaseService().startRetryTaskIfNeeded();
            return;
        }
        CompletableFuture.supplyAsync(this::collectSnapshot, scheduler::runSync)
                .thenApplyAsync(snapshot -> {
                    if (snapshot == null) {
                        logger.warning("Snapshot is null. Skipping metrics send.");
                        throw new IllegalStateException("Sync metric collection failed.");
                    }
                    long usedHeap = MemoryUtils.getUsedHeapBytes();
                    long committedHeap = MemoryUtils.getCommittedHeapBytes();

                    long totalDisk = ServerPulseProvider.get().getDiskRetriever().getTotalSpace();
                    long usableDisk = ServerPulseProvider.get().getDiskRetriever().getUsableSpace();

                    int minPing = ServerPulseProvider.get().getPingRetriever().getMinPing();
                    int maxPing = ServerPulseProvider.get().getPingRetriever().getMaxPing();
                    int avgPing = ServerPulseProvider.get().getPingRetriever().getAveragePing();

                    return buildPoints(snapshot, usedHeap, committedHeap, totalDisk, usableDisk, minPing, maxPing, avgPing);
                }, asyncExecutor).thenAcceptAsync(points -> {
                    if (!points.isEmpty()) {
                        try {
                            ServerPulseProvider.get().getDatabaseService().getWriteApi().writePoints(points);
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error sending metrics to InfluxDB...", e);
                        }
                    }
                }, asyncExecutor)
                         .exceptionally(ex -> {
                             logger.log(Level.SEVERE, "Failed metrics pipeline stage...", ex);
                             return null;
                         });
    }

    /**
     * Collects the current server metrics snapshot.
     *
     * @return A SyncMetricsSnapshot containing the current server metrics.
     * @throws IllegalStateException if called from a non-primary thread.
     */
    private SyncMetricsSnapshot collectSnapshot() {
        if (!platform.isPrimaryThread()) {
            logger.warning("Skipping metrics send because the thread is not primary thread...");
            throw new IllegalStateException("This method must be called on the main thread.");
        }
        try {
            double[] tps = ServerPulseProvider.get().getTPSRetriever().getTPS();
            int playerCount = platform.getOnlinePlayerCount();
            Map<String, WorldData> worldsData = platform.getWorldsData();

            return new SyncMetricsSnapshot(tps, playerCount, worldsData);
        } catch (Exception e) {
            logger.severe("Unexpected error during sync data collection: " + e.getMessage());
            // Return null or re-throw to signal failure to the CompletableFuture chain
            // Throwing is often cleaner as it goes directly to exceptionally()
            throw new RuntimeException("Sync data collection failed...", e);
        }
    }

    /**
     * Builds a list of InfluxDB points from the given metrics snapshot.
     *
     * @param snapshot The metrics snapshot to convert.
     * @param usedHeap The used heap memory in bytes.
     * @param committedHeap The committed heap memory in bytes.
     * @param totalDisk The total disk space in bytes.
     * @param usableDisk The usable disk space in bytes.
     * @return A list of InfluxDB points representing the metrics.
     */
    private List<Point> buildPoints(SyncMetricsSnapshot snapshot, long usedHeap, long committedHeap,
            long totalDisk, long usableDisk, int minPing, int maxPing, int avgPing) {
        List<Point> points = new ArrayList<>();

        String serverTag = configuration.getServerTag();
        String measurement = configuration.getMeasurementTable();

        Point generalPoint = Point.measurement(measurement)
                                  .addTag("server", serverTag)
                                  .addField("tps_1m", snapshot.getTps()[0])
                                  .addField("tps_5m", snapshot.getTps()[1])
                                  .addField("tps_15m", snapshot.getTps()[2])
                                  .addField("players_online", snapshot.getPlayerCount())
                                  .addField("used_memory", usedHeap)
                                  .addField("available_memory", committedHeap)
                                  .addField("total_disk_space", totalDisk)
                                  .addField("usable_disk_space", usableDisk)
                                  .addField("min_ping", minPing)
                                  .addField("max_ping", maxPing)
                                  .addField("avg_ping", avgPing)
                                  .time(Instant.now(), WritePrecision.NS);
        addConfigTags(generalPoint);
        points.add(generalPoint);

        for (Map.Entry<String, WorldData> entry : snapshot.getWorldData().entrySet()) {
            String worldName = entry.getKey();
            WorldData worldData = entry.getValue();

            Point worldPoint = Point.measurement(measurement)
                                    .addTag("server", serverTag)
                                    .addTag("world", worldName)
                                    .addField("entities_count", worldData.getEntities())
                                    .addField("loaded_chunks", worldData.getLoadedChunks())
                                    .time(Instant.now(), WritePrecision.NS);
            addConfigTags(worldPoint);
            points.add(worldPoint);
        }
        return points;
    }

    /**
     * Adds configuration tags to the given InfluxDB point.
     *
     * @param point The InfluxDB point to which tags will be added.
     */
    private void addConfigTags(Point point) {
        Map<String, String> tags = configuration.getTags();
        tags.forEach(point::addTag);
    }

    /**
     * Loads the metrics task with a configurable interval.
     */
    private void loadTask() {
        long intervalTicks = 20L * configuration.getMetricsInterval();
        if (intervalTicks <= 0) {
            logger.warning("Metrics interval is invalid, defaulting to 5 seconds.");
            intervalTicks = 20L * 5L;
        }
        scheduler.runTaskTimerAsync(this::collectAndSendMetrics, 0L, intervalTicks);
    }
}
