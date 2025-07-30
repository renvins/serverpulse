package it.renvins.serverpulse.common.metrics;

import it.renvins.serverpulse.api.metrics.IMSPTRetriever;

public class UnsupportedMSPTRetriever implements IMSPTRetriever {

    @Override
    public double getLastMSPT() {
        return 0.0;
    }

    @Override
    public double getAverageMSPT(int ticksCount) {
        return 0.0;
    }

    @Override
    public double getMinMSPT() {
        return 0.0;
    }

    @Override
    public double getMaxMSPT() {
        return 0.0;
    }
}
