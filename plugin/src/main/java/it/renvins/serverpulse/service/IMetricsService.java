package it.renvins.serverpulse.service;

import com.influxdb.client.write.Point;

public interface IMetricsService extends Service {
    void writePoint(Point point);
}
