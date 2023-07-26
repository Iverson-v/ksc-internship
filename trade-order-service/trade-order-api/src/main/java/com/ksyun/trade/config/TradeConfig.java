package com.ksyun.trade.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.ksyun.trade.mapper")
public class TradeConfig {

}
