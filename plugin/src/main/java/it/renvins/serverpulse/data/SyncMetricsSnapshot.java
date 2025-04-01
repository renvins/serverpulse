package it.renvins.serverpulse.data;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SyncMetricsSnapshot {

    private final double[] tps;
    private final int playerCount;
    private final Map<String, WorldData> worldData;
}
