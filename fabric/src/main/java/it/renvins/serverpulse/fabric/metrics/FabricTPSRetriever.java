package it.renvins.serverpulse.fabric.metrics;

import java.util.LinkedList;
import java.util.Queue;

import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.common.scheduler.TaskScheduler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FabricTPSRetriever implements ITPSRetriever {

    private static final int TICK_PER_SECOND = 20;
    private static final int TICK_PER_MIN = TICK_PER_SECOND * 60; // 1200
    private static final int TICK_FIVE_MIN = TICK_PER_MIN * 5;
    private static final int TICK_FIFTEEN_MIN = TICK_PER_MIN * 15;

    private static final int MAX_HISTORY_SIZE = TICK_FIFTEEN_MIN;

    private final Queue<Long> tickHistory = new LinkedList<>();

    private final TaskScheduler scheduler;

    private long lastTickTimeNano = -1;

    private double tps1m = 20.0;
    private double tps5m = 20.0;
    private double tps15m = 20.0;

    @Override
    public double[] getTPS() {
        calculateAverages();
        return new double[]{tps1m, tps5m, tps15m};
    }

    public void startTickMonitor() {
        lastTickTimeNano = System.nanoTime();

        scheduler.runTaskTimerAsync(() -> {
            long currentTimeNano = System.nanoTime();
            long elapsedTime = currentTimeNano - lastTickTimeNano;
            lastTickTimeNano = currentTimeNano;

            tickHistory.offer(elapsedTime);
            if (tickHistory.size() > MAX_HISTORY_SIZE) {
                tickHistory.poll();
            }
        }, 1, 1);
    }

    public void calculateAverages() {
        long sum1m = 0, sum5m = 0, sum15m = 0;
        int count1m = 0, count5m = 0, count15m = 0;

        Object[] durations = tickHistory.toArray();
        for (int i = durations.length - 1; i >= 0; i--) {
            if (!(durations[i] instanceof Long)) continue;
            long durationNano = (Long) durations[i];
            if (i < TICK_PER_MIN) {
                sum1m += durationNano;
                count1m++;
            }
            if (i < TICK_FIVE_MIN) {
                sum5m += durationNano;
                count5m++;
            }
            if (i < TICK_FIFTEEN_MIN) {
                sum15m += durationNano;
                count15m++;
            } else {
                break;
            }
        }

        tps1m = calculateTPSFromAvgNano(sum1m, count1m);
        tps5m = calculateTPSFromAvgNano(sum5m, count5m);
        tps15m = calculateTPSFromAvgNano(sum15m, count15m);
    }

    public double calculateTPSFromAvgNano(long totalNano, int count) {
        if (count == 0) {
            return 20.0;
        }
        double avgTickTimeMillis = (double) totalNano / count / 1_000_000.0;
        if (avgTickTimeMillis <= 0) {
            return 20.0;
        }
        // TPS: 1 second (1000ms) / avg tick time (ms)
        double tps = 1000.0 / avgTickTimeMillis;
        return Math.min(tps, 20.0);
    }
}
