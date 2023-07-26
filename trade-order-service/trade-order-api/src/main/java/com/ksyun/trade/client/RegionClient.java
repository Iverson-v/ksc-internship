package com.ksyun.trade.client;

import com.ksyun.trade.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RegionClient {

    //从配置文件中取值meta.url=campus.meta.ksyun.com:8090
    @Value("${meta.url}")
    private String url;

    public String getResponseById(int id){

        //要访问  http://campus.meta.ksyun.com:8090/online/region/name/{id}


        //链路跟踪
        log.info("进入Client进行转发请求。");

        //1.拼接url得到最终访问的地址
        String regionUrl=url+"/online/region/name/"+id;

        //2.httpclient请求地址返回
        String result=null;
        try {
            result = HttpClientUtils.get(regionUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //3.返回json字符串
        return result;
    }



}
