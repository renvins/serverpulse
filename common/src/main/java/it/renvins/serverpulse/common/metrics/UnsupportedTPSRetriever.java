package it.renvins.serverpulse.common.metrics;

import it.renvins.serverpulse.api.metrics.ITPSRetriever;

public class UnsupportedTPSRetriever implements ITPSRetriever {

    @Override
    public double[] getTPS() {
        // Proxies like BungeeCord or Velocity do not TPS, so we return a default value.
        return new double[]{0.0, 0.0, 0.0};
    }
}
