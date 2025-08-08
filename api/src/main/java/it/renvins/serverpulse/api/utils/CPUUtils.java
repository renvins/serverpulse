package it.renvins.serverpulse.api.utils;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;


public class CPUUtils {

    private final static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    /**
     * Get the system-wide CPU load ratio (0.0 to 1.0)
     * @return the system CPU load ratio
     */
    public static double getSystemCPULoadRatio() {
        return osBean.getCpuLoad();
    }

    /**
     * Get the JVM process CPU load ratio (0.0 to 1.0)
     * @return the process CPU load ratio
     */
    public static double getProcessCPULoadRatio() {
        return osBean.getProcessCpuLoad();
    }

    /**
     * Get the number of available processors
     * @return the number of available processors
     */
    public static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }
}
