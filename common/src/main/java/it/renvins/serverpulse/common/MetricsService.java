package it.renvins.serverpulse.common;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import it.renvins.serverpulse.api.data.AsyncMetricsSnapshot;
import it.renvins.serverpulse.api.service.IDatabaseService;
import it.renvins.serverpulse.api.data.SyncMetricsSnapshot;
import it.renvins.serverpulse.api.service.IMetricsService;
import it.renvins.serverpulse.common.logger.PulseLogger;
import it.renvins.serverpulse.common.metrics.LineProtocolFormatter;
import it.renvins.serverpulse.common.metrics.MetricsCollector;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;

public class MetricsService implements IMetricsService {

    private final PulseLogger logger;

    private final MetricsCollector collector;
    private final LineProtocolFormatter formatter;

    private final TaskScheduler scheduler;
    private final Executor asyncExecutor;
    private final IDatabaseService databaseService;

    public MetricsService(PulseLogger logger, MetricsCollector collector, LineProtocolFormatter formatter, TaskScheduler scheduler, IDatabaseService databaseService) {
        this.logger = logger;

        this.collector = collector;
        this.formatter = formatter;

        this.scheduler = scheduler;
        this.asyncExecutor = scheduler::runAsync;
        this.databaseService = databaseService;
    }

    @Override
    public void load() {
        logger.info("Loading metrics task...");
    }

    @Override
    public void collectAndSendMetrics() {
        if (!databaseService.isConnected()) {
            return;
        }
        CompletableFuture<SyncMetricsSnapshot> syncFuture = CompletableFuture.supplyAsync(
                collector::collectSyncSnapshot,
                scheduler.getSyncExecutor()
        );

        CompletableFuture<AsyncMetricsSnapshot> asyncFuture = CompletableFuture.supplyAsync(
                collector::collectAsyncSnapshot,
                asyncExecutor
        );

        syncFuture.thenCombineAsync(asyncFuture, (syncData, asyncData) -> {
            return formatter.format(syncData, asyncData);
        }, asyncExecutor).thenAcceptAsync(this::sendPoints, asyncExecutor)
                .exceptionally(ex -> {
                    logger.error("Failed during metrics collection or sending.", ex);
                    return null;
                });
    }

    private void sendPoints(List<String> points) {
        if (points == null || points.isEmpty()) {
            return;
        }

        try {
            String body = String.join("\n", points);
            databaseService.writeLineProtocol(body)
                .thenAccept(success -> {
                    if (!success) {
                        logger.error("Database reported failure on sending metrics.");
                    }
                });
        } catch (Exception e) {
            logger.error("Failed to send metrics data to the database.", e);
        }
    }
}
