package com.ksyun.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 *  1.EnableAsync(proxyTargetClass=true)配置代理为cglib代理(默认使用的是jdk动态代理)
 *
 *
 * @author ksc
 */
@SpringBootApplication
@EnableRetry
public class GateWayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GateWayApplication.class, args);
    }

}