package it.renvins.serverpulse.common.disk;

import java.io.File;
import it.renvins.serverpulse.api.metrics.IDiskRetriever;

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
