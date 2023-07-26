package com.ksyun.trade.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VoucherDeductDTO {

    private Integer orderId;
    private String voucherNo;
    private BigDecimal amount;
    private BigDecimal beforeDeductAmount;
    private BigDecimal afterDeductAmount;
}
