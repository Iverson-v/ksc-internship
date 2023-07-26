package com.ksyun.common.util.base;

import org.apache.commons.lang3.SystemUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * 运行时工具类
 * <p>
 * 1.取得当前进程PID, JVM参数
 * <p>
 * 2.注册JVM关闭钩子, 获得CPU核数
 * <p>
 * 3.通过StackTrace 获得当前方法的类名方法名，调用者的类名方法名(获取StackTrace有消耗，不要滥用)
 */
public class RuntimeUtil {

    // 操作系统类型及版本
    public static final String OS_NAME = SystemUtils.OS_NAME;

    public static final String OS_VERSION = SystemUtils.OS_VERSION;

    // e.g. x86_64
    public static final String OS_ARCH = SystemUtils.OS_ARCH;

    private static final RuntimeMXBean RUNTIME_MX_BEAN = ManagementFactory.getRuntimeMXBean();

    /**
     * 获得当前进程的PID
     * 当失败时返回-1
     */
    public static int getPid() {
        // format: "pid@hostname"
        String jvmName = RUNTIME_MX_BEAN.getName();
        String[] split = jvmName.split("@");
        if (split.length != 2) {
            return -1;
        }

        try {
            return Integer.parseInt(split[0]);
        } catch (Exception e) { // NOSONAR
            return -1;
        }
    }

    public static String getHostName() {
        // format: "pid@hostname"
        String jvmName = RUNTIME_MX_BEAN.getName();

        String[] split = jvmName.split("@");
        if (split.length != 2) {
            return "localhost";
        }

        return split[1];
    }


    /**
     * 返回应用启动到现在的毫秒数
     */
    public static long getVmUpTime() {
        return RUNTIME_MX_BEAN.getUptime();
    }

    public static long getVmStartTime() {
        return RUNTIME_MX_BEAN.getStartTime();
    }

    public static String getVmName() {
        return RUNTIME_MX_BEAN.getVmName();
    }

    public static String getVmVersion() {
        return RUNTIME_MX_BEAN.getVmVersion();
    }

    public static String getVmVendor() {
        return RUNTIME_MX_BEAN.getVmVendor();
    }

}
