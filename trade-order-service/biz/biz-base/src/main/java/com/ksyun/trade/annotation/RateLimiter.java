package com.ksyun.trade.annotation;

import com.ksyun.trade.constant.LimitType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ksc
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    String userValue() default "";

    LimitType limitType();

    //时间间隔，秒
    int timeInterval() default 5;

    /**
     * 最大请求数
     */
    int maxRequest() default 3;

}