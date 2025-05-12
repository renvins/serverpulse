package it.renvins.serverpulse.fabric.metrics;

import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

@RequiredArgsConstructor
public class FabricTPSRetriever implements ITPSRetriever {

    private static final int TICKS_PER_SECOND = 20;

    // Time windows in seconds
    private static final int ONE_MINUTE = 60;
    private static final int FIVE_MINUTES = 300;
    private static final int FIFTEEN_MINUTES = 900;

    // Maximum history to keep (15 minutes worth of ticks)
    private static final int MAX_SAMPLES = FIFTEEN_MINUTES * TICKS_PER_SECOND;

    // Ring buffer for storing tick times
    private final long[] tickTimes = new long[MAX_SAMPLES];
    private int tickIndex = 0;
    private boolean bufferFilled = false;

    // Last measurement time
    private long lastTickTime = -1;

    // Cache for TPS values
    private double tps1m = 20.0;
    private double tps5m = 20.0;
    private double tps15m = 20.0;

    @Override
    public double[] getTPS() {
        return new double[] { tps1m, tps5m, tps15m };
    }

    public void startTickMonitor() {
        lastTickTime = System.nanoTime();

        // Use ServerTickEvents instead of a scheduled task
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long now = System.nanoTime();

            // Only record after first tick
            if (lastTickTime > 0) {
                // Record this tick's time in nanoseconds
                tickTimes[tickIndex] = now - lastTickTime;

                // Update index in ring buffer
                tickIndex = (tickIndex + 1) % MAX_SAMPLES;

                // Mark buffer as filled once we wrap around
                if (tickIndex == 0) {
                    bufferFilled = true;
                }

                // Calculate TPS after recording the tick
                updateTPS();
            }

            lastTickTime = now;
        });
    }

    private void updateTPS() {
        // Convert seconds to ticks
        int oneMinTicks = ONE_MINUTE * TICKS_PER_SECOND;
        int fiveMinTicks = FIVE_MINUTES * TICKS_PER_SECOND;
        int fifteenMinTicks = FIFTEEN_MINUTES * TICKS_PER_SECOND;

        // Calculate the actual number of samples we have
        int sampleCount = bufferFilled ? MAX_SAMPLES : tickIndex;

        // Calculate TPS for each time window
        tps1m = calculateTPS(Math.min(oneMinTicks, sampleCount));
        tps5m = calculateTPS(Math.min(fiveMinTicks, sampleCount));
        tps15m = calculateTPS(Math.min(fifteenMinTicks, sampleCount));
    }

    private double calculateTPS(int samples) {
        if (samples <= 0) {
            return 20.0; // Default to 20 TPS if we have no samples
        }

        // Calculate total time for the samples
        long totalTime = 0;

        // Start from the most recent sample and go backwards
        for (int i = 0; i < samples; i++) {
            // Calculate index in the circular buffer, accounting for wrap-around
            int idx = (tickIndex - 1 - i + MAX_SAMPLES) % MAX_SAMPLES;
            totalTime += tickTimes[idx];
        }

        // Calculate TPS:
        // - totalTime is in nanoseconds for 'samples' ticks
        // - Convert to seconds to get time for 'samples' ticks
        // - Then calculate how many ticks would occur in 1 second
        double timeInSeconds = totalTime / 1_000_000_000.0;
        double ticksPerSecond = samples / timeInSeconds;

        // Clamp to maximum of 20 TPS
        return Math.min(ticksPerSecond, 20.0);
    }
}