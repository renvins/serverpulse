package it.renvins.serverpulse.common;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.data.LineProtocolPoint;
import it.renvins.serverpulse.api.utils.MemoryUtils;
import it.renvins.serverpulse.api.data.SyncMetricsSnapshot;
import it.renvins.serverpulse.api.data.WorldData;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.common.config.MetricsConfiguration;
import it.renvins.serverpulse.common.logger.PulseLogger;
import it.renvins.serverpulse.common.platform.Platform;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;

public class MetricsService implements IMetricsService {

    private final PulseLogger logger;

    private final Platform platform;
    private final MetricsConfiguration configuration;

    private final TaskScheduler scheduler;
    private final Executor asyncExecutor;

    public MetricsService(PulseLogger logger, Platform platform, MetricsConfiguration configuration, TaskScheduler scheduler) {
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
        if (!ServerPulseProvider.get().getDatabaseService().isConnected()) {
            return;
        }
        if (!ServerPulseProvider.get().getDatabaseService().ping()) {
            ServerPulseProvider.get().getDatabaseService().disconnect();
            ServerPulseProvider.get().getDatabaseService().startRetryTaskIfNeeded();
            return;
        }
        CompletableFuture.supplyAsync(this::collectSnapshot, scheduler.getSyncExecutor())
                .thenApplyAsync(snapshot -> {
                    if (snapshot == null) {
                        logger.warning("Snapshot is null. Skipping metrics send.");
                        throw new IllegalStateException("Sync metric collection failed.");
                    }

                    return buildPoints(snapshot, usedHeap, committedHeap, totalDisk, usableDisk, minPing, maxPing, avgPing);
                }, asyncExecutor).thenAcceptAsync(points -> {
                    if (!points.isEmpty()) {
                        try {
                            String body = String.join("\n", points);
                            ServerPulseProvider.get().getDatabaseService().writeLineProtocol(body);
                        } catch (Exception e) {
                            logger.error("Error sending metrics to InfluxDB...", e);
                        }
                    }
                }, asyncExecutor)
                         .exceptionally(ex -> {
                             logger.error( "Failed metrics pipeline stage...", ex);
                             return null;
                         });
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
    private List<String> buildPoints(SyncMetricsSnapshot snapshot, long usedHeap, long committedHeap,
            long totalDisk, long usableDisk, int minPing, int maxPing, int avgPing) {
        List<String> points = new ArrayList<>();

        String serverTag = configuration.getServerTag();
        String measurement = configuration.getMeasurementTable();

        LineProtocolPoint generalPoint = new LineProtocolPoint(measurement)
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
                                              .setTimestamp(Instant.now().toEpochMilli() * 1_000_000);
        addConfigTags(generalPoint);
        points.add(generalPoint.toLineProtocol());

        for (Map.Entry<String, WorldData> entry : snapshot.getWorldData().entrySet()) {
            String worldName = entry.getKey();
            WorldData worldData = entry.getValue();

            LineProtocolPoint worldPoint = new LineProtocolPoint(measurement)
                                    .addTag("server", serverTag)
                                    .addTag("world", worldName)
                                    .addField("entities_count", worldData.getEntities())
                                    .addField("loaded_chunks", worldData.getLoadedChunks())
                                    .setTimestamp(Instant.now().toEpochMilli() * 1_000_000);
            addConfigTags(worldPoint);
            points.add(worldPoint.toLineProtocol());
        }
        return points;
    }

    /**
     * Adds configuration tags to the given InfluxDB point.
     *
     * @param point The InfluxDB point to which tags will be added.
     */
    private void addConfigTags(LineProtocolPoint point) {
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
