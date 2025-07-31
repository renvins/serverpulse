package it.renvins.serverpulse.bukkit.metrics;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import it.renvins.serverpulse.common.metrics.CommonMSPTRetriever;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PaperMSPTRetriever extends CommonMSPTRetriever implements Listener {

    @EventHandler
    private void startTickMonitor(ServerTickEndEvent event) {
        addTickDuration(event.getTickDuration());
    }
}
