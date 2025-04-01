package it.renvins.serverpulse.metrics.impl;

import java.io.File;

import it.renvins.serverpulse.metrics.IDiskRetriever;

public class DiskRetriever implements IDiskRetriever {

    private final File path;

    public DiskRetriever(File path) {
        this.path = path;
    }

    @Override
    public long getTotalSpace() {
        return path.getTotalSpace();
    }

    @Override
    public long getUsableSpace() {
        return path.getUsableSpace();
    }
}
