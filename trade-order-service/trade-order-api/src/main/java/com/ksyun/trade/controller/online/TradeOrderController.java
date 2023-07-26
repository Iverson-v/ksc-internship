package com.ksyun.trade.controller.online;

import com.ksyun.trade.client.RegionClient;
import com.ksyun.trade.client.UserClient;
import com.ksyun.trade.dto.OrderForGoodsDTO;
import com.ksyun.trade.dto.OrderForRegionDTO;
import com.ksyun.trade.dto.ResultResponseDTO;
import com.ksyun.trade.entity.KscVoucherDeduct;
import com.ksyun.trade.mapper.KscVoucherDeductMapper;
import com.ksyun.trade.rest.RestResult;
import com.ksyun.trade.service.TradeOrderService;
import com.ksyun.trade.utils.jaksonutils.JacksonUtil;
import com.ksyun.trade.utils.redisutils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/online/trade_order", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
@Api(tags="api接口")
public class TradeOrderController {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private TradeOrderService orderService;
    @Autowired
    private UserClient userClient;
    @Autowired
    private RegionClient regionClient;



    @RequestMapping("/{id}")
    @ApiOperation("查询订单详情")
    public ResultResponseDTO query(@PathVariable("id") Integer id) {
        //链路跟踪
        log.info("查询订单详情功能进入TradeOrderController。");


        //要访问  http://campus.meta.ksyun.com:8090/online/user/{id}
        //1.去访问这个接口。得到响应体放到jsonStr中
        //String jsonStr = userClient.getResponseById(id);

        CompletableFuture<String> completableFuture1=CompletableFuture.supplyAsync(()->{
            return userClient.getResponseById(id);
        });

        //CompletableFuture<String> jsonStrFuture = getUserJsonStringById(id);

        //jsonStr= {"code":200,"msg":"ok","requestId":"3f016ac7-4a6c-4d7e-918f-6146eae681ce","descr":null,
        // "data":{"id":10,"username":"user10","email":"user10@example.com","phone":"10000531913","address":"杭州市中山路世茂城97号"}}


        //2.调用service方法，获得封装了data字段   但是data中user的值需要在这个jsonStr中获取然后封装
        //OrderForGoodsDTO orderForGoodsDTO = orderService.parseToOrderForGoodsDTO(id);
        CompletableFuture<OrderForGoodsDTO> completableFuture2=CompletableFuture.supplyAsync(()->{
            return orderService.parseToOrderForGoodsDTO(id);
        });
        //CompletableFuture<OrderForGoodsDTO> orderForGoodsDTOFuture = getOrderForGoodsDTO(id);

        //3.把orderForGoodsDTO和jsonStr中的信息封装到ResultResponseDTO中
        // 等待第一和第二个方法的返回值
        //String jsonStr = jsonStrFuture.join();
        //OrderForGoodsDTO orderForGoodsDTO = orderForGoodsDTOFuture.join();
        //ResultResponseDTO resultResponseDTO = orderService.parseToResultResponseDTO(jsonStr, orderForGoodsDTO);
        CompletableFuture<ResultResponseDTO> resultCompletableFuture=completableFuture1.thenCombine(completableFuture2,(jsonStr,orderForGoodsDTO)->{
            return orderService.parseToResultResponseDTO(jsonStr, orderForGoodsDTO);
        });

        //4.返回resultResponseDTO
        //return resultResponseDTO;
        return resultCompletableFuture.join();
    }


    @RequestMapping("/region/{regionId}")
    @ApiOperation("查询机房详情")
    public String queryRegion(@PathVariable("regionId") Integer id) {
        //链路跟踪
        log.info("查询机房详情进入GatewayController进行处理。");


        //要访问  http://campus.meta.ksyun.com:8090/online/user/{id}
        //通过regionClient方法去访问该url，并且返回最终结果的json字符串



        String jsonStr="";
        for (int i = 0; i < 100; i++) {//重试
            Object regions = redisUtils.hashGet("regions", String.valueOf(id));

            if(regions==null){
                //redis没查到，去远程查
                jsonStr= regionClient.getResponseById(id);

                HashMap<String, Object> stringObjectHashMap = new HashMap<>();
                stringObjectHashMap.put(String.valueOf(id),jsonStr);
                //4.把json字符串转化为对象orderForRegionDTO
                OrderForRegionDTO orderForRegionDTO = JacksonUtil.toBean(jsonStr, OrderForRegionDTO.class);
                if(orderForRegionDTO.getCode()==200){
                    redisUtils.hashPutAll("regions",stringObjectHashMap);
                }

            }else {
                jsonStr=(String) regions;
            }
            OrderForRegionDTO orderForRegionDTO = JacksonUtil.toBean(jsonStr, OrderForRegionDTO.class);
            if (orderForRegionDTO.getCode()==200){
                break;
            }
        }

        return jsonStr;
    }

    @PostMapping("/deduct")
    @ApiOperation("订单优惠券抵扣")
    public Object addDeduct(@RequestBody KscVoucherDeduct kscVoucherDeduct) {

        //入参{
        //     "orderId":1,
        //     "voucherNo":"TEST123456",
        //     "amount":10
        //}
        //出参{
        //    "code": 200,
        //    "requestId": "6ebc37ee-b46b-43c9-ac2d-c2117b149b27",
        //    "msg": "ok"
        //}

        //先根据orderid查询

//        System.out.println(kscVoucherDeduct.getVoucherNo());
//        System.out.println(kscVoucherDeduct.getOrderId());
//        System.out.println(kscVoucherDeduct.getAmount());

//        kscVoucherDeductMapper.insert(kscVoucherDeduct);

        //链路跟踪
        log.info("订单优惠券抵扣功能进入GatewayController进行处理。");
        boolean flag = orderService.updateVoucherDeduct(kscVoucherDeduct);
        if (flag){
            return RestResult.success();
        }else
            return RestResult.failure();



    }

}
