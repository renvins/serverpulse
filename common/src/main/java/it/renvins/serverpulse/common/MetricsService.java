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
