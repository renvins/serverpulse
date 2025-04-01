package it.renvins.serverpulse.metrics;

public interface IMemoryRetriever {

    /**
     * Retrieves the amount of used heap memory in bytes.
     *
     * @return The used heap memory in bytes.
     */
    long getUsedHeapBytes();

    /**
     * Retrieves the amount of committed heap memory in bytes.
     *
     * @return The committed heap memory in bytes.
     */
    long getCommittedHeapBytes();
}
