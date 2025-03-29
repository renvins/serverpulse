package it.renvins.serverpulse.task;

import it.renvins.serverpulse.ServerPulsePlugin;
import it.renvins.serverpulse.service.IMetricsService;

public class MetricsTask implements Runnable {

    private final ServerPulsePlugin plugin;
    private final IMetricsService metricsService;

    public MetricsTask(ServerPulsePlugin plugin,  IMetricsService metricsService) {
        this.plugin = plugin;
        this.metricsService = metricsService;
    }

    @Override
    public void run() {
        // !-- THIS CODE IS RUNNING ASYNC --!
        if (!plugin.isEnabled()) {
            return;
        }
        metricsService.sendMetrics();
    }
}
