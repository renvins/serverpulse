package it.renvins.serverpulse.api.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

public class MemoryUtils {

    private final static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    /**
     * Gets the total heap memory in bytes.
     *
     * @return the total heap memory in bytes
     */
    public static long getUsedHeapBytes() {
        return memoryMXBean.getHeapMemoryUsage().getUsed();
    }

    /**
     * Gets the maximum heap memory in bytes.
     *
     * @return the maximum heap memory in bytes
     */
    public static long getCommittedHeapBytes() {
        return memoryMXBean.getHeapMemoryUsage().getCommitted();
    }
}
