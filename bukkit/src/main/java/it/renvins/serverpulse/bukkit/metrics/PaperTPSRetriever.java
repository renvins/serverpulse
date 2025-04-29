package it.renvins.serverpulse.bukkit.metrics;

import it.renvins.serverpulse.api.metrics.ITPSRetriever;
import org.bukkit.Bukkit;

public class PaperTPSRetriever implements ITPSRetriever {

    @Override
    public double[] getTPS() {
        return Bukkit.getTPS();
    }
}
