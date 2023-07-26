package com.ksyun.trade.vo;

import lombok.Data;

@Data
public class QueryDeductVo {
    private int code;

    /**
     * 异常信息
     */
    private String msg;

    private String requestId;
}
