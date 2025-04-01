package it.renvins.serverpulse.metrics.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import it.renvins.serverpulse.metrics.IMemoryRetriever;

public class MemoryRetriever implements IMemoryRetriever {

    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    @Override
    public long getUsedHeapBytes() {
        return memoryMXBean.getHeapMemoryUsage().getUsed();
    }

    @Override
    public long getCommittedHeapBytes() {
        return memoryMXBean.getHeapMemoryUsage().getCommitted();
    }
}