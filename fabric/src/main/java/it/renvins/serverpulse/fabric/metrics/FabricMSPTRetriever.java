package it.renvins.serverpulse.fabric.metrics;

import it.renvins.serverpulse.common.metrics.CommonMSPTRetriever;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FabricMSPTRetriever extends CommonMSPTRetriever {

    private long lastTickTimeNano = -1;

    public void startMSPTMonitor() {
        lastTickTimeNano = System.nanoTime();

        ServerTickEvents.END_SERVER_TICK.register(tick -> {
            long currentTimeNano = System.nanoTime();
            long elapsedNano = currentTimeNano - lastTickTimeNano;
            lastTickTimeNano = currentTimeNano;

            addTickDuration(elapsedNano / 1_000_000.0); // Convert to milliseconds
        });
    }
}
