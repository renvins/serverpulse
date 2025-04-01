package it.renvins.serverpulse.metrics;

public interface IDiskRetriever {

    /**
     * Retrieves the total space of the disk in bytes.
     *
     * @return The total space of the disk in bytes.
     */
    long getTotalSpace();

    /**
     * Retrieves the usable space of the disk in bytes.
     *
     * @return The usable space of the disk in bytes.
     */
    long getUsableSpace();
}
