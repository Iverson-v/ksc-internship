package com.ksyun.trade.controller.online;

import com.ksyun.trade.rest.RestResult;
import com.ksyun.trade.utils.HttpClientUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/online")
@Slf4j
@Api(tags="负载均衡接口")
public class LbController {

    //指向urls的下标
    private  int point=0;
    List<String> urls = new ArrayList<>();

    {

        // 初始化
        urls.add("http://campus.query1.ksyun.com:8089/online/trade_order/2");
        urls.add("http://campus.query2.ksyun.com:8089/online/trade_order/2");
        urls.add("http://campus.query3.ksyun.com:8089/online/trade_order/2");
        urls.add("http://campus.query4.ksyun.com:8089/online/trade_order/2");
        urls.add("http://campus.query5.ksyun.com:8089/online/trade_order/2");
    }



    public LbController() {

    }

    @GetMapping("/random")
    @ResponseBody
    @ApiOperation("随机算法")
    public RestResult getRandomUrl() {

        // 生成随机数0-4
        Random random = new Random();
        int index = random.nextInt(urls.size());

        // Return the random URL
        return RestResult.success().data(urls.get(index));
    }



    @GetMapping("/round")
    @ResponseBody
    @ApiOperation("轮询算法")
    public RestResult getRoundUrl() {


        int listSize=urls.size();
        point=(point+1)%listSize;

        // Return the random URL
        return RestResult.success().data(urls.get(point));
    }







    @GetMapping("/lb")
    @ResponseBody
    @ApiOperation("请求httpclient")
    public String callHttpClient() throws Exception {

        List<String> urls = new ArrayList<>();
        // Initialize the list of URLs with the provided data
        urls.add("http://campus.query1.ksyun.com:8089/online/trade_order/2");
        urls.add("http://campus.query2.ksyun.com:8089/online/trade_order/2");
        urls.add("http://campus.query3.ksyun.com:8089/online/trade_order/2");
        urls.add("http://campus.query4.ksyun.com:8089/online/trade_order/2");
        urls.add("http://campus.query5.ksyun.com:8089/online/trade_order/2");
        // Generate a random index within the range of the URL list
        Random random = new Random();
        int index = random.nextInt(urls.size());
        String url = urls.get(index);

        //发送请求
        String userInfo = HttpClientUtils.get(url);
//        String userInfo = HttpClientUtils.get("http://localhost:8089/order/query?keyId=1");
        // Return the random URL
//        return RestResult.success().data(userInfo);
        return userInfo;
    }




}
