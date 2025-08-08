package it.renvins.serverpulse.common.metrics;

import it.renvins.serverpulse.api.data.AsyncMetricsSnapshot;
import it.renvins.serverpulse.api.data.LineProtocolPoint;
import it.renvins.serverpulse.api.data.SyncMetricsSnapshot;
import it.renvins.serverpulse.api.data.WorldData;
import it.renvins.serverpulse.common.config.GeneralConfiguration;
import it.renvins.serverpulse.common.config.MetricsConfiguration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Formats raw metric snapshots into InfluxDB Line Protocol strings.
 * Its single responsibility is to handle the formatting logic.
 */
public class LineProtocolFormatter {

    private final MetricsConfiguration metricsConfig;

    public LineProtocolFormatter(GeneralConfiguration generalConfig) {
        this.metricsConfig = new MetricsConfiguration(generalConfig);
    }

    public List<String> format(SyncMetricsSnapshot syncData, AsyncMetricsSnapshot asyncData) {
        List<LineProtocolPoint> points = new ArrayList<>();
        long timestamp = Instant.now().toEpochMilli() * 1_000_000;

        LineProtocolPoint generalPoint = new LineProtocolPoint(metricsConfig.getMeasurementTable())
                .addTag("server", metricsConfig.getServerTag())
                // Sync data
                .addField("players_online", syncData.getPlayerCount())
                // Async data
                .addField("used_memory", asyncData.getUsedHeap())
                .addField("available_memory", asyncData.getCommitedHeap())
                .addField("total_disk_space", asyncData.getTotalDisk())
                .addField("usable_disk_space", asyncData.getUsableDisk())
                .addField("min_ping", asyncData.getMinPing())
                .addField("max_ping", asyncData.getMaxPing())
                .addField("avg_ping", asyncData.getAvgPing())
                .addField("system_cpu_load_ratio", asyncData.getSystemCpuLoadRatio())
                .addField("process_cpu_load_ratio", asyncData.getProcessCpuLoadRatio())
                .addField("available_processors", asyncData.getAvailableProcessors())
                .setTimestamp(timestamp);

        if (syncData.getTps()[0] != 0.0 && syncData.getTps()[1] != 0.0 && syncData.getTps()[2] != 0.0) {
            generalPoint.addField("tps_1m", syncData.getTps()[0])
                        .addField("tps_5m", syncData.getTps()[1])
                        .addField("tps_15m", syncData.getTps()[2]);
        }

        if (asyncData.getMspt1m() != 0.0 && asyncData.getMspt5m() != 0.0 && asyncData.getMspt15m() != 0.0 &&
            asyncData.getLastMSPT() != 0.0 && asyncData.getMinMSPT() != 0.0 && asyncData.getMaxMSPT() != 0.0) {
                generalPoint.addField("mspt_1m", asyncData.getMspt1m())
                        .addField("mspt_5m", asyncData.getMspt5m())
                        .addField("mspt_15m", asyncData.getMspt15m())
                        .addField("last_mspt", asyncData.getLastMSPT())
                        .addField("min_mspt", asyncData.getMinMSPT())
                        .addField("max_mspt", asyncData.getMaxMSPT());
        }

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