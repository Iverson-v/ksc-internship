package com.ksyun.trade.bootstrap;


import com.ksyun.common.util.base.RuntimeUtil;
import com.ksyun.common.util.concurrent.AbortPolicyWithReport;
import com.ksyun.common.util.concurrent.NamedThreadFactory;
import com.ksyun.common.util.concurrent.ThreadPoolBuilder;
import com.ksyun.req.trace.filter.Slf4jMDCServletFilter;
import com.ksyun.trade.bo.EnvInfo;
import com.ksyun.trade.bo.JvmInfo;
import com.ksyun.trade.bo.OsInfo;
import com.ksyun.trade.constant.Constant;
import com.ksyun.trade.constant.Env;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
@Configuration
public class AppConfig {

    @Autowired
    private Environment environment;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(20);
        return taskScheduler;
    }

    @Bean
    public FilterRegistrationBean getRequestIdFilter() {
        FilterRegistrationBean filter = new FilterRegistrationBean();
        filter.setFilter(new Slf4jMDCServletFilter());             //配置设置过滤器生效。
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filter;
    }

    @Bean(name = "encodingFilter")
    public FilterRegistrationBean createEncodingFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding(StandardCharsets.UTF_8.name());
        registration.setFilter(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean(name = "envInfo")
    public EnvInfo getEnvInfo() {
        String appName = environment.getProperty("info.app.name");
        String appVer = environment.getProperty("info.app.ver");
        String appEnvStr = environment.getProperty("info.app.env");

        Env env = Env.transformEnv(appEnvStr);
        String envTagStr = String.format("【%s - %s】", appName, appEnvStr);

        EnvInfo envInfo = new EnvInfo();
        envInfo.setName(appName);
        envInfo.setVer(appVer);
        envInfo.setEnvStr(appEnvStr);
        envInfo.setEnv(env);
        envInfo.setEnvTagStr(envTagStr);
        return envInfo;
    }

    @Bean(name = "osInfo")
    public OsInfo getOsInfo() {
        OsInfo osInfo = new OsInfo();
        osInfo.setName(RuntimeUtil.OS_NAME);
        osInfo.setVer(RuntimeUtil.OS_VERSION);
        osInfo.setArch(RuntimeUtil.OS_ARCH);
        osInfo.setAvailableProcessors(Constant.NCPU);
        return osInfo;
    }

    @Bean(name = "jvmInfo")
    public JvmInfo getJvmInfo() {
        JvmInfo jvmInfo = new JvmInfo();
        jvmInfo.setVmName(RuntimeUtil.getVmName());
        jvmInfo.setVmVer(RuntimeUtil.getVmVersion());
        jvmInfo.setVmVendor(RuntimeUtil.getVmVendor());
        jvmInfo.setJdkVer(SystemUtils.JAVA_SPECIFICATION_VERSION);
        return jvmInfo;
    }

    @Bean(name = "appDefaultPoolExecutor")
    public ThreadPoolExecutor getAppDefaultPoolExecutor() {
        String prefix = "default";
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolBuilder.FixedThreadPoolBuilder()
                .setThreadFactory(new NamedThreadFactory(prefix))
                .setPoolSize((Constant.NCPU - 1) * 2).setRejectHanlder(new AbortPolicyWithReport(prefix)).build();
        log.info("appDefaultPoolExecutor init...");
        return threadPoolExecutor;
    }


}

