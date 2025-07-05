package it.renvins.serverpulse.common.metrics;

import it.renvins.serverpulse.api.data.AsyncMetricsSnapshot;
import it.renvins.serverpulse.api.data.LineProtocolPoint;
import it.renvins.serverpulse.api.data.SyncMetricsSnapshot;
import it.renvins.serverpulse.api.data.WorldData;
import it.renvins.serverpulse.common.config.MetricsConfiguration;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Formats raw metric snapshots into InfluxDB Line Protocol strings.
 * Its single responsibility is to handle the formatting logic.
 */
@RequiredArgsConstructor
public class LineProtocolFormatter {

    private final MetricsConfiguration metricsConfig;

    public List<String> format(SyncMetricsSnapshot syncData, AsyncMetricsSnapshot asyncData) {
        List<LineProtocolPoint> points = new ArrayList<>();
        long timestamp = Instant.now().toEpochMilli() * 1_000_000;

        LineProtocolPoint generalPoint = new LineProtocolPoint(metricsConfig.getMeasurementTable())
                .addTag("server", metricsConfig.getServerTag())
                // Sync data
                .addField("tps_1m", syncData.getTps()[0])
                .addField("tps_5m", syncData.getTps()[1])
                .addField("tps_15m", syncData.getTps()[2])
                .addField("players_online", syncData.getPlayerCount())
                // Async data
                .addField("used_memory", asyncData.getUsedHeap())
                .addField("available_memory", asyncData.getCommitedHeap())
                .addField("total_disk_space", asyncData.getTotalDisk())
                .addField("usable_disk_space", asyncData.getUsableDisk())
                .addField("min_ping", asyncData.getMinPing())
                .addField("max_ping", asyncData.getMaxPing())
                .addField("avg_ping", asyncData.getAvgPing())
                .setTimestamp(timestamp);

        addConfigTags(generalPoint);
        points.add(generalPoint);

        for (Map.Entry<String, WorldData> entry : syncData.getWorldData().entrySet()) {
            String worldName = entry.getKey();
            WorldData worldData = entry.getValue();

            LineProtocolPoint worldPoint = new LineProtocolPoint(metricsConfig.getMeasurementTable())
                    .addTag("server", metricsConfig.getServerTag())
                    .addTag("world", worldName)
                    .addField("entities_count", worldData.getEntities())
                    .addField("loaded_chunks", worldData.getLoadedChunks())
                    .setTimestamp(timestamp);

            addConfigTags(worldPoint);
            points.add(worldPoint);
        }

        return points.stream()
                .map(LineProtocolPoint::toLineProtocol)
                .collect(Collectors.toList());
    }

    private void addConfigTags(LineProtocolPoint point) {
        metricsConfig.getTags().forEach(point::addTag);
    }
}