package it.renvins.serverpulse.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class MetricsConfiguration {

    private final String serverTag;
    private final String measurementTable;
    private final long metricsInterval;
    private final Map<String, String> tags;

}
