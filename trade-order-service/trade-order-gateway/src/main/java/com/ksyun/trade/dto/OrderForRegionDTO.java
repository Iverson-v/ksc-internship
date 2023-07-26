package com.ksyun.trade.dto;

import lombok.Data;

@Data
public class OrderForRegionDTO {
    private int code;

    /**
     * 异常信息
     */
    private String msg;

    private String requestId;
    private String descr;

    /**
     * 返回结果
     */
    private String  data;
}
