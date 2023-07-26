package com.ksyun.trade.controller.inner;

import com.ksyun.trade.bo.AppInfo;
import com.ksyun.trade.bo.EnvInfo;
import com.ksyun.trade.rest.RestResult;
import com.ksyun.trade.service.AppInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ksc
 */
@RestController
public class AppInfoController {

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private EnvInfo envInfo;

    @RequestMapping(value = "/app/info", method = {RequestMethod.GET} ,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public RestResult getAppInfo() {
        AppInfo appInfo = appInfoService.build();
        return RestResult.success().data(appInfo);
    }

    @RequestMapping(value = "/", method = {RequestMethod.GET}, produces = {"application/json;charset=UTF-8"})
    public RestResult hello() {
        String appName = envInfo.getName();
        return RestResult.success().data("Hello," + appName + "!");
    }
}