package it.renvins.serverpulse.common.metrics;

import it.renvins.serverpulse.api.metrics.IMSPTRetriever;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CommonMSPTRetriever implements IMSPTRetriever {

    private final Queue<Double> ticks = new ConcurrentLinkedDeque<>();
    private static final int MAX_HISTORY_SIZE = 15 * 60 * 20; // 15 minutes at 20 ticks per second

    public void addTickDuration(double tickDuration) {
        ticks.offer(tickDuration);

        if (ticks.size() > MAX_HISTORY_SIZE) {
            ticks.poll(); // Remove the oldest tick duration if we exceed the max size
        }
    }

    @Override
    public double getLastMSPT() {
        if (ticks.isEmpty()) {
            return 0.0;
        }
        Double[] durations = ticks.toArray(new Double[0]);
        return durations[durations.length - 1];
    }

    @Override
    public double getAverageMSPT(int ticksCount) {
        if (ticks.isEmpty() || ticksCount <= 0) {
            return 0.0;
        }
        List<Double> recentTicks = new ArrayList<>(ticks);
        int startIndex = Math.max(0, recentTicks.size() - ticksCount);

        List<Double> relevantTicks = recentTicks.subList(startIndex, recentTicks.size());

        if (relevantTicks.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (double duration : relevantTicks) {
            sum += duration;
        }

        return sum / relevantTicks.size();
    }

    @Override
    public double getMinMSPT() {
        if (ticks.isEmpty()) {
            return 0.0;
        }
        return ticks.stream().min(Double::compareTo).orElse(0.0);
    }

    @Override
    public double getMaxMSPT() {
        if (ticks.isEmpty()) {
            return 0.0;
        }
        return ticks.stream().max(Double::compareTo).orElse(0.0);
    }
}
