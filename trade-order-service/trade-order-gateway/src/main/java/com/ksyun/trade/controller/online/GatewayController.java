package com.ksyun.trade.controller.online;

import com.ksyun.trade.dto.OrderForGoodsDTO;
import com.ksyun.trade.dto.OrderForRegionDTO;
import com.ksyun.trade.rest.RestResult;
import com.ksyun.trade.vo.QueryDeductVo;
import com.ksyun.trade.vo.QueryOrderVo;
import com.ksyun.trade.dto.VoucherDeductDTO;
import com.ksyun.trade.service.GatewayService;
import com.ksyun.trade.utils.HttpClientUtils;
import com.ksyun.trade.utils.jaksonutils.JacksonUtil;
import com.ksyun.trade.vo.QueryRegionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

import static com.ksyun.req.trace.ReqTraceConsts.TRACE_KEY;

@RestController
@Slf4j
@Api(tags="网关接口")
public class GatewayController {
    @Autowired
    private GatewayService gatewayService;

    /**
     * 查询订单详情 (GET)
     */
    @RequestMapping(value = "/online/queryOrderInfo", produces = "application/json")
    @ApiOperation("查询订单详情")
    public Object queryOrderInfo(Integer id) {

        //链路跟踪
        log.info("查询订单详情功能进入GatewayController进行请求转发。");

        //1.获取负载均衡的ip和端口号 roundUrl=http://campus.query1.ksyun.com:8089
        String roundUrl = gatewayService.loadLalancing();

        //2.截取发送请求的服务器ip=campus.query1.ksyun.com
        String ip=roundUrl.split(":")[1].substring(2);

        //3.拼接接口地址得到请求url
        //roundUrl= http://localhost:8089/online/trade_order/10
        roundUrl=roundUrl+"/online/trade_order/"+id;


        //4.发送httpclient请求。请求对应服务器，并返回json字符串
        String userInfo=null;
        try {
            //还得设置请求头把"X-KSY-REQUEST-ID"添加上。
            userInfo = HttpClientUtils.getAndSetHeader(roundUrl,"UTF-8",
                    30000,30000,"X-KSY-REQUEST-ID", MDC.get(TRACE_KEY));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //5.反序列化userInfo，
        QueryOrderVo queryOrderVo = JacksonUtil.toBean(userInfo, QueryOrderVo.class);

        //6.封装属性 "upsteam"，
        OrderForGoodsDTO orderForGoodsDTO = queryOrderVo.getOrderForGoodsDTO();
        orderForGoodsDTO.setUpsteam(ip);
        queryOrderVo.setOrderForGoodsDTO(orderForGoodsDTO);


        //7.最终封装结果返回
        if(queryOrderVo.getCode()==200){

            //result.setRequestId(queryOrderVo.getRequestId());
            return RestResult.success().data(orderForGoodsDTO);
        }else{
            return RestResult.failure();
        }

    }

    /**
     * 根据机房Id查询机房名称 (GET)
     */
    @RequestMapping(value = "/online/queryRegionName", produces = "application/json")
    @ApiOperation("根据机房id查询机房详情")
    public Object queryRegionName(Integer regionId) {

        //链路跟踪
        log.info("根据机房id查询机房详情功能进入GatewayController进行请求转发。");
        //1.根据负载均衡获取分摊目标机器的目标ip和端口号  roundUrl=http://localhost:8089
        String roundUrl = gatewayService.loadLalancing();

        //2.拼接目标地址，获得url。
        //roundUrl= http://localhost:8089/online/trade_order/region/10
        roundUrl=roundUrl+"/online/trade_order/region/"+regionId;


        //3.通过httpclient请求该url，获得json字符串。
        String userRegionInfo=null;
        try {
            userRegionInfo = HttpClientUtils.getAndSetHeader(roundUrl,"UTF-8",
                    30000,30000,"X-KSY-REQUEST-ID", MDC.get(TRACE_KEY));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //4.把json字符串转化为对象orderForRegionDTO
        OrderForRegionDTO orderForRegionDTO = JacksonUtil.toBean(userRegionInfo, OrderForRegionDTO.class);

        //5.创建最后出参的vo类的对象。
        QueryRegionVo queryRegionVo = new QueryRegionVo();

        //6.把orderForRegionDTO中属性赋值给出参的vo对象。
        BeanUtils.copyProperties(orderForRegionDTO,queryRegionVo);


        //7.返回vo对象
        return queryRegionVo;


    }

    /**
     * 订单优惠券抵扣 (POST json)
     */
    @PostMapping (value = "/online/voucher/deduct", produces = "application/json")
    @ApiOperation("优惠券接口")
    public Object deduct(@RequestBody VoucherDeductDTO voucherDeductDTO) {

        //链路跟踪
        log.info("优惠券接口功能进入GatewayController进行请求转发。");

        //1.根据负载均衡获取分摊目标机器的目标ip和端口号  roundUrl=http://localhost:8089
        String roundUrl = gatewayService.loadLalancing();

        //2.拼接目标地址，获得url。
        //roundUrl= http://localhost:8089/online/trade_order/deduct
        roundUrl=roundUrl+"/online/trade_order/deduct";

        //3.把voucherDeductDTO再次序列化
        String voucherDeductDTOJsonStr = JacksonUtil.toJsonStr(voucherDeductDTO);

        //4.通过httpclient发送post请求该roundUrl
        String userDeductInfo=null;
        try {
            //定义post请求格式为application/json，默认是application/x-www-form-urlencoded。
            userDeductInfo = HttpClientUtils.postSetHeaderAndSetBody(roundUrl,voucherDeductDTOJsonStr,"application/json",
                    "UTF-8", 30000,30000,"X-KSY-REQUEST-ID", MDC.get(TRACE_KEY));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //5,转化为对应VO类出参

        //4.把json字符串转化为对象orderForRegionDTO
        RestResult result = JacksonUtil.toBean(userDeductInfo, RestResult.class);
        QueryDeductVo queryDeductVo=new QueryDeductVo();
        BeanUtils.copyProperties(result,queryDeductVo);



        return queryDeductVo;


    }

    /**
     * 基于Redis实现漏桶限流算法，并在API调用上体现
     */
    @RequestMapping(value = "/online/listUpstreamInfo", produces = "application/json")
    @ApiOperation("漏桶限流算法")
    public Object listUpstreamInfo() {
        //出参:
        //{
        //    "code": 200,
        //    "msg": "ok",
        //    "requestId": "6ebc37ee-b46b-43c9-ac2d-c2117b149b27",
        //    "data": ["campus.query1.ksyun.com", "campus.query2.ksyun.com"]
        //}
        //
        //报错时结构体:
        //{
        //     "code": 429,
        //     "requestId": "6ebc37ee-b46b-43c9-ac2d-c2117b149b27",
        //     "msg": "对不起, 系统压力过大, 请稍后再试!"
        //}

        //链路跟踪
        log.info("漏桶限流算法接口进入GatewayController处理。");

        boolean flag = gatewayService.leakyBucket1();
        if(flag){
            String[] dataValue={"campus.query1.ksyun.com", "campus.query2.ksyun.com"};
            return RestResult.success().data(dataValue);
        }else
            ResponseEntity.status(200);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(RestResult.failure().code(429).msg("对不起，系统压力过大，请稍后再试！"));
    }

}
