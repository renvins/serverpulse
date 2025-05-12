package it.renvins.serverpulse.bukkit.metrics;

import java.util.LinkedList;
import java.util.Queue;

import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.bukkit.ServerPulseBukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitTPSRetriever implements ITPSRetriever {

    private static final int TICKS_PER_SECOND = 20;
    private static final int ONE_MINUTE_TICKS = 60 * TICKS_PER_SECOND; // 1200
    private static final int FIVE_MINUTES_TICKS = 5 * ONE_MINUTE_TICKS; // 6000
    private static final int FIFTEEN_MINUTES_TICKS = 15 * ONE_MINUTE_TICKS; // 18000

    private static final int MAX_HISTORY_SIZE = FIFTEEN_MINUTES_TICKS;

    // Queue for FIFO
    private final Queue<Long> tickDurations = new LinkedList<>();
    private long lastTickTimeNano = -1;

    private double tps1m = 20.0;
    private double tps5m = 20.0;
    private double tps15m = 20.0;

    private final ServerPulseBukkit plugin;

    public BukkitTPSRetriever(ServerPulseBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public double[] getTPS() {
        calculateAverages();
        return new double[]{tps1m, tps5m, tps15m};
    }

    public void startTickMonitor() {
        lastTickTimeNano = System.nanoTime();

        new BukkitRunnable() {

            @Override
            public void run() {
                long currentTimeNano = System.nanoTime();
                long elapsedNano = currentTimeNano - lastTickTimeNano;
                lastTickTimeNano = currentTimeNano;

                tickDurations.offer(elapsedNano);
                if (tickDurations.size() > MAX_HISTORY_SIZE) {
                    tickDurations.poll();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
    private void calculateAverages() {
        double sum1m = 0, sum5m = 0, sum15m = 0;
        int count1m = 0, count5m = 0, count15m = 0;

        int i = 0;
        Object[] durationsArray = tickDurations.toArray();

        for (int j = durationsArray.length - 1; j >= 0; j--) {
            if (!(durationsArray[j] instanceof Long)) continue;
            long durationNano = (Long) durationsArray[j];

            if (i < ONE_MINUTE_TICKS) {
                sum1m += durationNano;
                count1m++;
            }
            if (i < FIVE_MINUTES_TICKS) {
                sum5m += durationNano;
                count5m++;
            }
            if (i < FIFTEEN_MINUTES_TICKS) {
                sum15m += durationNano;
                count15m++;
            } else {
                break;
            }
            i++;
        }

        tps1m = calculateTPSFromAvgNano(sum1m, count1m);
        tps5m = calculateTPSFromAvgNano(sum5m, count5m);
        tps15m = calculateTPSFromAvgNano(sum15m, count15m);
    }

    private double calculateTPSFromAvgNano(double totalNano, int count) {
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
