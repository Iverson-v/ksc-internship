package com.ksyun.trade.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TradeProductConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemNo;

    private String itemName;

    private String unit;

    private Integer value;

}
