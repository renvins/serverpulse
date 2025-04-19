package it.renvins.serverpulse.api.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

public class MemoryUtils {

    private final static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    public static long getUsedHeapBytes() {
        return memoryMXBean.getHeapMemoryUsage().getUsed();
    }

    public static long getCommittedHeapBytes() {
        return memoryMXBean.getHeapMemoryUsage().getCommitted();
    }
}
