package com.ksyun.trade.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResultResponseDTO {

    private int code;

    private String msg;
    private String requestId;

    private String desc;


    @JsonProperty("data")//序列化的时候修改字段名为data
    private OrderForGoodsDTO orderForGoodsDTO;

}
