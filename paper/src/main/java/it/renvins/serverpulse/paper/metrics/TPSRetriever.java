package it.renvins.serverpulse.paper.metrics;

import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import org.bukkit.Bukkit;

public class TPSRetriever implements ITPSRetriever {

    @Override
    public double[] getTPS() {
        return Bukkit.getTPS();
    }
}
