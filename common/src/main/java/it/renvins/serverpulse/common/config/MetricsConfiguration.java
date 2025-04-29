package it.renvins.serverpulse.common.config;

import java.util.Map;

public interface MetricsConfiguration {

    String getServerTag();
    String getMeasurementTable();
    long getMetricsInterval();
    Map<String, String> getTags();

}
