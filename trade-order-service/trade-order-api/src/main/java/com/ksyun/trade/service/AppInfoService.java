package com.ksyun.trade.service;

import com.ksyun.common.util.base.HostNameUtil;
import com.ksyun.common.util.base.MemoryMXBeanUtils;
import com.ksyun.common.util.base.OperatingSystemMXBeanUtils;
import com.ksyun.common.util.base.RuntimeUtil;
import com.ksyun.common.util.time.DateFormatUtil;
import com.ksyun.trade.bo.AppInfo;
import com.ksyun.trade.bo.EnvInfo;
import com.ksyun.trade.bo.JvmInfo;
import com.ksyun.trade.bo.OsInfo;
import com.ksyun.trade.util.UtilAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ksc
 */
@Service
public class AppInfoService {

    @Autowired
    private EnvInfo envInfo;

    @Autowired
    private OsInfo osInfo;

    @Autowired
    private JvmInfo jvmInfo;

    public AppInfo build() {
        AppInfo appInfo = new AppInfo();

        appInfo.setPid(RuntimeUtil.getPid());
        appInfo.setIp("IP");
        appInfo.setHostName(HostNameUtil.getHostName());

        appInfo.setEnvInfo(envInfo);
        appInfo.setOsInfo(osInfo);
        appInfo.setJvmInfo(jvmInfo);
        appInfo.setRuntimeInfo(getRuntimeInfo());

        return appInfo;
    }

    private AppInfo.MemoryInfo getMemoryInfo() {
        AppInfo.MemoryInfo memoryInfo = new AppInfo.MemoryInfo();
        memoryInfo.setInit(MemoryMXBeanUtils.getHeapMemoryInitInMBytes() + "M");
        memoryInfo.setUsed(MemoryMXBeanUtils.getHeapMemoryUsedInMBytes() + "M");
        memoryInfo.setCommitted(MemoryMXBeanUtils.getHeapMemoryCommittedInMBytes() + "M");
        memoryInfo.setMax(MemoryMXBeanUtils.getHeapMemoryMaxInMBytes() + "M");
        return memoryInfo;
    }

    private AppInfo.RuntimeInfo getRuntimeInfo() {
        AppInfo.RuntimeInfo runtimeInfo = new AppInfo.RuntimeInfo();
        runtimeInfo.setVmStartTime(DateFormatUtil.formatFriendlyTimeSpanByNow(RuntimeUtil.getVmStartTime()));
        runtimeInfo.setVmUpTime(UtilAll.toPrettyString(RuntimeUtil.getVmUpTime()));
        runtimeInfo.setSystemLoadAverage(OperatingSystemMXBeanUtils.getSystemLoadAverage());
        runtimeInfo.setMemoryInfo(getMemoryInfo());
        return runtimeInfo;
    }

}


