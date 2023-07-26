package com.ksyun.trade.dto;

import lombok.Data;

@Data
public class UseTechnologyDTO {
    /**
     * 是否用到负载均衡
     */
    private boolean usedLb = false;
    /**
     * 是否用到缓存
     */
    private boolean usedSimpleCache = false;
    /**
     * 是否用到多级缓存
     */
    private boolean usedMultiLevelCache = false;
    /**
     * 是否用到多线程
     */
    private boolean usedMultiThread = false;

    /**
     * 是否用到判断日志级别, 来打印日志
     */
    private boolean usedLogLevel = false;

    /**
     * 调用链路可追踪
     */
    private boolean usedTraceId = false;
;
}
