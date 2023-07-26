package com.ksyun.trade.client;

import com.ksyun.trade.utils.HttpClientUtils;
import com.ksyun.trade.utils.redisutils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class UserClient {

    @Autowired
    private RedisUtils redisUtils;

    //从配置文件中取值meta.url=campus.meta.ksyun.com:8090
    @Value("${meta.url}")
    private String url;



    public String getResponseById(int id) {
        //  http://campus.meta.ksyun.com:8090/online/user/10

        //链路跟踪
        log.info("进入Client进行转发请求。");
        //1.拼接url得到最终访问的地址
        String regionUrl = url + "/online/user/" + id;

        //2.httpclient请求地址返回
        String result = null;
        try {
            result =(String) redisUtils.hashGet("user_id", String.valueOf(id));
            if(result==null){//如果缓存没查到
                result = HttpClientUtils.get(regionUrl);
                HashMap<String, Object> stringObjectHashMap = new HashMap<>();
                stringObjectHashMap.put(String.valueOf(id),result);
                redisUtils.hashPutAll("user_id",stringObjectHashMap);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //3.返回json字符串
        return result;
////////////////////////////////////////////////

    }
}
