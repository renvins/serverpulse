package it.renvins.serverpulse.bukkit.metrics;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import it.renvins.serverpulse.bukkit.ServerPulseBukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitTPSRetriever implements ITPSRetriever {

    private static final int MAX_HISTORY_SIZE = 15 * 60 * 20; // 15 minutes at 20 ticks per second

    // Queue for FIFO
    private final Queue<Long> tickTimestamps = new ConcurrentLinkedDeque<>();
    private final ServerPulseBukkit plugin;

    public BukkitTPSRetriever(ServerPulseBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public double[] getTPS() {
        long currentTime = System.currentTimeMillis();

        long oneMinuteAgo = currentTime - (60 * 1000);
        long fiveMinutesAgo = currentTime - (5 * 60 * 1000);
        long fifteenMinutesAgo = currentTime - (15 * 60 * 1000);

        int ticks1m = 0;
        int ticks5m = 0;
        int ticks15m = 0;

        for (long timeStamp : tickTimestamps) {
            if (timeStamp >= oneMinuteAgo) {
                ticks1m++;
            }
            if (timeStamp >= fiveMinutesAgo) {
                ticks5m++;
            }
            if (timeStamp >= fifteenMinutesAgo) {
                ticks15m++;
            }
        }
        double tps1m = ticks1m / 60.0;
        double tps5m = ticks5m / 300.0;
        double tps15m = ticks15m / 900.0;

        return new double[]{Math.min(20.0, tps1m), Math.min(20.0, tps5m), Math.min(20.0, tps15m)};
    }

    public void startTickMonitor() {

        new BukkitRunnable() {

            @Override
            public void run() {
                tickTimestamps.offer(System.currentTimeMillis());

                if (tickTimestamps.size() > MAX_HISTORY_SIZE) {
                    tickTimestamps.poll();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
}
