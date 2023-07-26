package com.ksyun.trade.service;

import com.ksyun.trade.BaseSpringAllTest;
import com.ksyun.trade.utils.redisutils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class GatewayServiceTest extends BaseSpringAllTest {

    @Autowired
    RedisUtils redisUtils;
    @Autowired
    private GatewayService gatewayService;

    @Test
    public void query() throws InterruptedException {
//        long l = System.currentTimeMillis();
//        System.out.println(l);
//        Thread.sleep(200);
//        long l2 = System.currentTimeMillis();
//        System.out.println(l2);
//        long l3=l2-l;
//        System.out.println(l3);
//        System.out.println(l3/1000f);
        Object asdas = redisUtils.strGet("asdas");
        System.out.println(asdas);
//


    }
}
