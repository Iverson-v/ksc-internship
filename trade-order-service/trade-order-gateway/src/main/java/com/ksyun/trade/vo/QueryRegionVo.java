package com.ksyun.trade.vo;

import lombok.Data;


@Data
public class QueryRegionVo{


    /**
     * 异常代码
     */
    private int code;

    /**
     * 异常信息
     */
    private String msg;

    private String requestId;

    /**
     * 返回结果
     */
    private String  data;
}
