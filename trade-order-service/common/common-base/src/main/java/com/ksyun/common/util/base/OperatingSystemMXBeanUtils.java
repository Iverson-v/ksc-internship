package com.ksyun.common.util.base;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * 
 */
public final class OperatingSystemMXBeanUtils {

    private static final OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN = ManagementFactory.getOperatingSystemMXBean();

    private OperatingSystemMXBeanUtils() {
    }

    public static double getSystemLoadAverage() {
        return OPERATING_SYSTEM_MX_BEAN.getSystemLoadAverage();
    }

}
