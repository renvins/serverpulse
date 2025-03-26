package it.renvins.serverpulse.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;

public interface IDatabaseService extends Service {

    InfluxDBClient getClient();
    WriteApi getWriteApi();
}
