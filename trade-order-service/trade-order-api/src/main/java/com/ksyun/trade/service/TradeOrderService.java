package com.ksyun.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.trade.client.UserClient;
import com.ksyun.trade.dto.OrderForGoodsDTO;
import com.ksyun.trade.dto.ResultResponseDTO;
import com.ksyun.trade.entity.KscTradeOrder;
import com.ksyun.trade.entity.KscTradeProductConfig;
import com.ksyun.trade.entity.KscVoucherDeduct;
import com.ksyun.trade.mapper.KscTradeOrderMapper;
import com.ksyun.trade.mapper.KscVoucherDeductMapper;
import com.ksyun.trade.utils.HttpClientUtils;
import com.ksyun.trade.utils.redisutils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TradeOrderService {

    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private KscVoucherDeductMapper kscVoucherDeductMapper;
    @Resource
    private KscTradeOrderMapper kscTradeOrderMapper;

    @Autowired
    private KscTradeOrderService kscTradeOrderService;

    @Autowired
    private KscTradeProductConfigService kscTradeProductConfigService;

    public OrderForGoodsDTO parseToOrderForGoodsDTO(Integer id) {
        //链路跟踪
        log.info("进入Service层：TradeOrderService。");

        //1.创建返回的DTO类对象
        OrderForGoodsDTO orderForGoodsDTO = new OrderForGoodsDTO();

        //2.通过数据库查询两张表，并且把结果封装到两个pojo对象上。
        KscTradeOrder kscTradeOrder=null;
        kscTradeOrder = (KscTradeOrder)redisUtils.hashGet("kscTradeOrder", String.valueOf(id));
        if(kscTradeOrder==null){
            //如果缓存没查到去数据库查
            kscTradeOrder= kscTradeOrderService.getById(id);
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            stringObjectHashMap.put(String.valueOf(id),kscTradeOrder);
            redisUtils.hashPutAll("kscTradeOrder",stringObjectHashMap);

        }

        KscTradeProductConfig kscTradeProductConfig=null;
        kscTradeProductConfig = (KscTradeProductConfig)redisUtils.hashGet("kscTradeProductConfig", String.valueOf(id));
        if(kscTradeProductConfig==null){
            //如果缓存没查到去数据库查
            kscTradeProductConfig= kscTradeProductConfigService.getById(id);
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            stringObjectHashMap.put(String.valueOf(id),kscTradeProductConfig);
            redisUtils.hashPutAll("kscTradeProductConfig",stringObjectHashMap);

        }




        //3.给返回的DTO对象赋值id和priceValue
        orderForGoodsDTO.setId(id);
        orderForGoodsDTO.setPriceValue(kscTradeOrder.getPriceValue());


        //4.通过httpclient访问http://campus.meta.ksyun.com:8090/online/region/list，得到json字符串
        String regionList = null;
        try {
            regionList =(String) redisUtils.strGet("region_list");
            if(regionList==null){//如果缓存没查到
                regionList = HttpClientUtils.get("http://campus.meta.ksyun.com:8090/online/region/list");
                redisUtils.strSet("region_list",regionList);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //5.在返回的json数组中找到对应id的Region
        String name = null;
        String code = null;
        try {
            // 初始化ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // 将JSON字符串解析为JsonNode对象
            JsonNode jsonNode = objectMapper.readTree(regionList);

            // 获取data数组
            JsonNode dataArray = jsonNode.get("data");


            // 遍历data数组查找nid为id的元素
            for (JsonNode element : dataArray) {
                int nId = element.get("id").asInt();
                if (id == nId) {
                    name = element.get("name").asText();
                    code = element.get("code").asText();
                    break; // 如果找到nid为id的元素，退出循环
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //6.给DTO内部类Region赋值code和name属性
        orderForGoodsDTO.setRegion(new OrderForGoodsDTO.Region(code, name));

        //7.给DTO内部List<Config>赋值
        OrderForGoodsDTO.Config config = new OrderForGoodsDTO.Config();
        config.setItemNo(kscTradeProductConfig.getItemNo());
        config.setItemName(kscTradeProductConfig.getItemName());
        config.setUnit(kscTradeProductConfig.getUnit());
        config.setValue(kscTradeProductConfig.getValue());
        ArrayList<OrderForGoodsDTO.Config> listConfig = new ArrayList<>();
        listConfig.add(config);
        orderForGoodsDTO.setConfigs(listConfig);

        //8.返回最终包装好的DTO，这里只包装了一部分属性
        return orderForGoodsDTO;
    }

    public ResultResponseDTO parseToResultResponseDTO(String jsonStr, OrderForGoodsDTO orderForGoodsDTO) {

        //1.创建返回的DTO类对象。
        ResultResponseDTO resultResponseDTO=new ResultResponseDTO();

        //2.获得 code  msg  requestId字段以及data字段里面user的值  方便后面赋值给orderForGoodsDTO，和resultResponseDTO
        int code =500;
        String msg =null;
        String requestId=null;

        String username=null;
        String email = null;
        String phone = null;
        String address =null;
        try {
            // 初始化ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // 将JSON字符串解析为JsonNode对象
            JsonNode jsonNode = objectMapper.readTree(jsonStr);

            // 获取code、msg和requestId字段的值
            code = jsonNode.get("code").asInt();
            msg = jsonNode.get("msg").asText();
            requestId = jsonNode.get("requestId").asText();

            // 获取data对象
            JsonNode dataNode = jsonNode.get("data");
            // 获取username、email、phone和address字段的值
            username = dataNode.get("username").asText();
            email = dataNode.get("email").asText();
            phone = dataNode.get("phone").asText();
            address = dataNode.get("address").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //3.封装user信息以及code  msg  requestId信息 以及User信息
        orderForGoodsDTO.setUser(new OrderForGoodsDTO.User(username,email,phone,address));
        resultResponseDTO.setCode(code);
        resultResponseDTO.setMsg(msg);
        resultResponseDTO.setRequestId(requestId);
        resultResponseDTO.setOrderForGoodsDTO(orderForGoodsDTO);

        //4.返回
        return resultResponseDTO;
    }


    public boolean updateVoucherDeduct(KscVoucherDeduct kscVoucherDeduct){
        //入参{
        //     "orderId":1,
        //     "voucherNo":"TEST123456",
        //     "amount":10
        //}
        //出参{
        //    "code": 200,
        //    "requestId": "6ebc37ee-b46b-43c9-ac2d-c2117b149b27",
        //    "msg": "ok"

        //链路跟踪
        log.info("进入Service层：TradeOrderService。");

        //1.先查询voucherNo,这个字段辨识一张消费券,如果查询有这个voucherNo就不用插入了，已经更新成功了。
        String voucherNo = kscVoucherDeduct.getVoucherNo();


        //redis缓存
        List<Object> voucherList = redisUtils.listGet("voucher_no", 0, -1);
        boolean flag=false;
        for (Object obj : voucherList) {
            if(voucherNo.equals(obj.toString())){
                flag=true;//表示查到了这个冗余消费券
                break;
            }
        }

        //缓存查不到去数据库查询
        if(!flag){
            //根据voucherNo查询数据库中是否有这个优惠券，有了表示冗余了。不处理
            QueryWrapper<KscVoucherDeduct> wrapper = new QueryWrapper<>();
            wrapper.eq("voucher_no",voucherNo);
            KscVoucherDeduct kscVoucherDeduct1 = kscVoucherDeductMapper.selectOne(wrapper);
            if(kscVoucherDeduct1!=null){
                flag=true;//表示查到了这个冗余消费券
                //查到的数据放到redis中
                redisUtils.listRightPush("voucher_no",kscVoucherDeduct1.getVoucherNo());
            }
        }




        //2.判断是否已经插入成功了，如果kscVoucherDeduct1不为空表示不需要插入了，直接返回。
        if(flag){//如果查到了冗余消费券就直接返回
            return false;
        }

        //3.判断这个orderId在数据库中是否存在。如果存在就叠加消费券，如果不存在就去数据库查这个orderId的订单price。
        Integer orderId = kscVoucherDeduct.getOrderId();


        //redis缓存
        //如果在缓存中查到了orderId，取beforeDeductAmount的值。
        BigDecimal beforeDeductAmount =(BigDecimal)redisUtils.strGet(String.valueOf(orderId));
        flag=false;
        if(beforeDeductAmount!=null){
            flag=true;
        }else {
            //缓存查不到去数据库查询，
            QueryWrapper<KscVoucherDeduct> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("order_id",orderId).orderByDesc("create_time").last("limit 1");
            //根据voucherNo查询数据库中是否有这个优惠券，有了表示冗余了。不处理
            KscVoucherDeduct kscVoucherDeduct1 = kscVoucherDeductMapper.selectOne(wrapper1);
            if(kscVoucherDeduct1!=null){
                //如果在数据库中查到了orderId，取beforeDeductAmount的值。
                flag=true;
                beforeDeductAmount=kscVoucherDeduct1.getAfterDeductAmount();

                //查到的数据放到redis中
                redisUtils.strSetWithTimeLimit(String.valueOf(orderId),
                        beforeDeductAmount,
                        1, TimeUnit.HOURS);
            }
        }






        //4.orderId不存在的话。要去数据库1查询，
        if(!flag){
            //要先从数据库1查询价格，
            try{
                KscTradeOrder kscTradeOrder = kscTradeOrderMapper.selectById(kscVoucherDeduct.getOrderId());
                beforeDeductAmount = kscTradeOrder.getPriceValue();
            }catch (NullPointerException e){
                e.printStackTrace();
                log.info("orderId没有在ksc_trade_order中查询到！该订单不存在！");
                return false;
            }


        }

        //5.把上一次的价格放到数据库3的before字段，把上一次的价格减去amount放到after字段。
        BigDecimal afterDeductAmount = beforeDeductAmount.subtract(kscVoucherDeduct.getAmount());
        kscVoucherDeduct.setBeforeDeductAmount(beforeDeductAmount);
        kscVoucherDeduct.setAfterDeductAmount(afterDeductAmount);

        //6.插入数据

        //往redis中存入key为voucher_no的值，值为after_deduct_amount的值。
        redisUtils.listRightPush("voucher_no",kscVoucherDeduct.getVoucherNo());
        redisUtils.strSetWithTimeLimit(String.valueOf(kscVoucherDeduct.getOrderId()),
                kscVoucherDeduct.getAfterDeductAmount(),
                1, TimeUnit.HOURS);
        kscVoucherDeductMapper.insert(kscVoucherDeduct);
        return true;






    }


}