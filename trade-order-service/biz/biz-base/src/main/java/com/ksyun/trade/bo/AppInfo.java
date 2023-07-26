package com.ksyun.trade.bo;

import lombok.Data;

/**
 * @author ksc
 */
@Data
public class AppInfo {

    private Integer pid;

    private String hostName;

    private String ip;

    private EnvInfo envInfo;

    private OsInfo osInfo;

    private JvmInfo jvmInfo;

    private RuntimeInfo runtimeInfo;

    @Data
    public static class RuntimeInfo {

        private String vmStartTime;

        private String vmUpTime;

        private Double systemLoadAverage;

        private MemoryInfo memoryInfo;

    }

    @Data
    public static class MemoryInfo {

        private String init;

        private String used;

        private String committed;

        private String max;
    }
}
