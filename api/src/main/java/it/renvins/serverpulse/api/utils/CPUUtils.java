package it.renvins.serverpulse.api.utils;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;


public class CPUUtils {

    private final static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    /**
     * Get the CPU load ratio
     * @return the CPU load ratio
     */
    public static double getCPULoadRatio() {
        return osBean.getCpuLoad();
    }

    /**
     * Get the CPU seconds used by the current process
     * @return the CPU seconds used by the current process
     */
    public static double getProcessCPUMs() {
        long nanos = osBean.getProcessCpuTime();
        if (nanos < 0) {
            return -1.0d;
        }
        return nanos / 1_000_000;
    }
}
