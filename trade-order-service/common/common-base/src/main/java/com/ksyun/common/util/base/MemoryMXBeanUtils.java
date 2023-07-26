package com.ksyun.common.util.base;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;


/**
 * 
 */
public class MemoryMXBeanUtils {

    private static final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    private MemoryMXBeanUtils() {
    }

    public static long getHeapMemoryUsed() {
        return memoryMXBean.getHeapMemoryUsage().getUsed();
    }

    public static long getHeapMemoryMax() {
        return memoryMXBean.getHeapMemoryUsage().getMax();
    }

    public static long getHeapMemoryCommitted() {
        return memoryMXBean.getHeapMemoryUsage().getCommitted();
    }

    public static long getHeapMemoryInit() {
        return memoryMXBean.getHeapMemoryUsage().getInit();
    }

    public static double getHeapMemoryUsedInMBytes() {
        return ((double) getHeapMemoryUsed() / (1024 * 1024));
    }

    public static double getHeapMemoryMaxInMBytes() {
        return ((double) getHeapMemoryMax() / (1024 * 1024));
    }

    public static double getHeapMemoryCommittedInMBytes() {
        return ((double) getHeapMemoryCommitted() / (1024 * 1024));
    }

    public static double getHeapMemoryInitInMBytes() {
        return ((double) getHeapMemoryInit() / (1024 * 1024));
    }

}