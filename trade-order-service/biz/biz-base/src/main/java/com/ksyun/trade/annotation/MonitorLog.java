package com.ksyun.trade.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 用于需要拦截打印日志和添加监控,用于接口服务类*
**/
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MonitorLog {
}
