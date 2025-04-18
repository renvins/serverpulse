package it.renvins.serverpulse.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.logging.Level;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import it.renvins.serverpulse.ServerPulseLoader;
import it.renvins.serverpulse.ServerPulsePlugin;
import it.renvins.serverpulse.config.CustomConfig;
import it.renvins.serverpulse.data.SyncMetricsSnapshot;
import it.renvins.serverpulse.data.WorldData;
import it.renvins.serverpulse.metrics.IDiskRetriever;
import it.renvins.serverpulse.metrics.IMemoryRetriever;
import it.renvins.serverpulse.metrics.IPingRetriever;
import it.renvins.serverpulse.metrics.impl.DiskRetriever;
import it.renvins.serverpulse.metrics.impl.MemoryRetriever;
import it.renvins.serverpulse.metrics.impl.PingRetriever;
import it.renvins.serverpulse.service.IDatabaseService;
import it.renvins.serverpulse.service.IMetricsService;
import org.bukkit.Bukkit;

public class MetricsService implements IMetricsService {

    private final ServerPulsePlugin plugin;
    private final CustomConfig config;

    private final IDatabaseService databaseService;

    private final IMemoryRetriever memoryRetriever;
    private final IDiskRetriever diskRetriever;
    private final IPingRetriever pingRetriever;

    private final Executor asyncExecutor;

    public MetricsService(ServerPulsePlugin plugin, CustomConfig config, IDatabaseService databaseService) {
        this.plugin = plugin;
        this.config = config;
        this.databaseService = databaseService;

        this.memoryRetriever = new MemoryRetriever();
        this.diskRetriever = new DiskRetriever(plugin.getDataFolder());
        this.pingRetriever = new PingRetriever();

        this.asyncExecutor = task -> Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Override
    public void load() {
        ServerPulseLoader.LOGGER.info("Loading metrics task...");
        loadTask();
    }

    @Override
    public void collectAndSendMetrics() {
        if (!databaseService.isConnected() || databaseService.getWriteApi() == null) {
            return;
        }
        if (!databaseService.ping()) {
            databaseService.disconnect();
            databaseService.startRetryTaskIfNeeded();
            return;
        }
        CompletableFuture.supplyAsync(this::collectSnapshot, Bukkit.getScheduler().getMainThreadExecutor(plugin))
                .thenApplyAsync(snapshot -> {
                    if (snapshot == null) {
                        ServerPulseLoader.LOGGER.warning("Snapshot is null. Skipping metrics send.");
                        throw new IllegalStateException("Sync metric collection failed.");
                    }
                    long usedHeap = memoryRetriever.getUsedHeapBytes();
                    long committedHeap = memoryRetriever.getCommittedHeapBytes();

                    long totalDisk = diskRetriever.getTotalSpace();
                    long usableDisk = diskRetriever.getUsableSpace();

                    int minPing = pingRetriever.getMinPing();
                    int maxPing = pingRetriever.getMaxPing();
                    int avgPing = pingRetriever.getAveragePing();

                    return buildPoints(snapshot, usedHeap, committedHeap, totalDisk, usableDisk, minPing, maxPing, avgPing);
                }, asyncExecutor).thenAcceptAsync(points -> {
                    if (!points.isEmpty()) {
                        try {
                            databaseService.getWriteApi().writePoints(points);
                        } catch (Exception e) {
                            ServerPulseLoader.LOGGER.log(Level.SEVERE, "Error sending metrics to InfluxDB...", e);
                        }
                    }
                }, asyncExecutor)
                         .exceptionally(ex -> {
                             ServerPulseLoader.LOGGER.log(Level.SEVERE, "Failed metrics pipeline stage...", ex);
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
        if (!plugin.getServer().isPrimaryThread()) {
            ServerPulseLoader.LOGGER.warning("Skipping metrics send because the thread is not primary thread...");
            throw new IllegalStateException("This method must be called on the main thread.");
        }
        try {
            double[] tps = plugin.getServer().getTPS();
            int playerCount = Bukkit.getOnlinePlayers().size();
            Map<String, WorldData> worldData = new HashMap<>();

            Bukkit.getWorlds().forEach(world -> {
                WorldData data = new WorldData(world.getEntities().size(), world.getPlayers().size());
                worldData.put(world.getName(), data);
            });
            return new SyncMetricsSnapshot(tps, playerCount, worldData);
        } catch (Exception e) {
            ServerPulseLoader.LOGGER.severe("Unexpected error during sync data collection: " + e.getMessage());
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

        String serverTag = config.getConfig().getString("metrics.tags.server");
        String measurement = config.getConfig().getString("metrics.influxdb.table");

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
        Map<String, Object> tags = config.getConfig().getConfigurationSection("metrics.tags").getValues(false);
        tags.forEach((key, value) -> {
            if (value instanceof String && !key.equalsIgnoreCase("server") && !key.equalsIgnoreCase("world")) {
                point.addTag(key, value.toString());
            }
        });
    }

    /**
     * Loads the metrics task with a configurable interval.
     */
    private void loadTask() {
        long intervalTicks = 20L * config.getConfig().getLong("metrics.interval", 5L);
        if (intervalTicks <= 0) {
            ServerPulseLoader.LOGGER.warning("Metrics interval is invalid, defaulting to 5 seconds.");
            intervalTicks = 20L * 5L;
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::collectAndSendMetrics, 0L, intervalTicks);
    }
}
