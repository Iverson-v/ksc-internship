package com.ksyun.trade.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksyun.trade.dto.OrderForGoodsDTO;
import lombok.Data;

@Data
public class QueryOrderVo {

    private int code;

    private String msg;
    private String requestId;

    private String desc;


    @JsonProperty("data")//序列化的时候修改字段名为data
    private OrderForGoodsDTO orderForGoodsDTO;

}
