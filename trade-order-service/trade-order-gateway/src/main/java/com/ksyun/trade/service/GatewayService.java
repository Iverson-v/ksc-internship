package com.ksyun.trade.service;

import com.ksyun.trade.utils.redisutils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@Slf4j
public class GatewayService implements InitializingBean {


    private   int point=0;//指向urls的下标，作为轮询的指针
    private  final String BUCKETKEY = "rate_limiter";
    private  final int CAPACITY = 5; // Bucket capacity
    private  final int RATE = 5;      // Requests per second
    private  long lastRequestTime = System.currentTimeMillis();

    private double water=10;



    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${actions}")
    private String actions;
    //actions=http://campus.query1.ksyun.com:8089,http://campus.query2.ksyun.com:8089


    //轮询选择一个服务器作为目的地址，模拟路由 (负载均衡) 获取接口
    public String loadLalancing() {
        //链路跟踪
        log.info("进入service层轮询算法");

        //1.把所有需要轮询的服务器地址放到urls中
        String[] urls = actions.split(",");

        //2.轮询算法
        int arrSize=urls.length;
        point=(point+1)%arrSize;

        //3.获得最终要选择的url
        //roundUrl=http://campus.query1.ksyun.com:8089
        String roundUrl = urls[point];

        roundUrl="http://localhost:8089";//这里在本地测试！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！

        // 4. 返回请求转发的url
        return roundUrl;
    }



    //漏桶算法
    //版本一不满足要求
    public  boolean leakyBucket() {
        long currentTime = System.currentTimeMillis();


        if(!redisUtils.hasKey(BUCKETKEY)){
            redisUtils.strSet(BUCKETKEY, lastRequestTime);
        }

        lastRequestTime = (long)redisUtils.strGet(BUCKETKEY);

        long timeDiff = currentTime - lastRequestTime;

       // System.out.println("间隔："+timeDiff/1000f+"s");

        water = Math.max(0, water-((double)timeDiff * RATE / 1000));

        if (water+1 <= CAPACITY) {
            water+=1;
            redisUtils.strSet(BUCKETKEY, currentTime);
            return true;
        }


        redisUtils.strSet(BUCKETKEY, currentTime);
        return false;
    }


    //漏桶算法
    //版本二不满足要求，没使用lua脚本。不符合要求。
    public  boolean leakyBucket1() {
        //1.获取当前线程时间，单位毫秒数
        long currentTime = System.currentTimeMillis();

        //2.从redis中取出BUCKETKEY，表示当前线程执行到这里，桶里还有多少水滴。
        double water = Double.parseDouble(String.valueOf(redisUtils.strGet(BUCKETKEY)));

        //3.计算上一次限流到现在经过了多长时间
        long timeDiff = currentTime - lastRequestTime;

        //4.在这段时间里，水桶之前的水，减去这段时间流出来的水，为现在水桶中的水。
        water = Math.max(0, water-(timeDiff * RATE / 1000.0));

        //5.如果现在水桶中的水比最大容量小，满足要求放行。把这个请求放到桶中
        if (water<= CAPACITY-1) {
            lastRequestTime=currentTime;//lastRequestTime总是为最新的限流时间，这点非常重要！！！！！！！！
            redisUtils.strSet(BUCKETKEY,(water+1)+"");//把这个请求放到桶中，水桶容量加1

            return true;
        }

        return false;
    }


    //漏桶算法
    //使用lua脚本。符合要求。
    public boolean leakyBucket2() {
        //链路跟踪
        log.info("漏桶限流算法接口进入GatewayService处理。");
        // 获取当前时间，单位为毫秒
        long currentTime = System.currentTimeMillis();

        String luaScript="local bucket_key = KEYS[1]\n" +
                "local capacity = tonumber(ARGV[1])\n" +
                "local rate = tonumber(ARGV[2])\n" +
                "local current_time = tonumber(ARGV[3])\n" +
                "\n" +
                "local water = tonumber(redis.call(\"GET\", bucket_key)) or 0\n" +
                "local time_diff = current_time - (tonumber(redis.call(\"GET\", bucket_key .. \":last_request_time\")) or current_time)\n" +
                "local leaked_water = time_diff * rate / 1000\n" +
                "water = math.max(0, water - leaked_water)\n" +
                "\n" +
                "if water <= capacity - 1 then\n" +
                "    water = water + 1\n" +
                "    redis.call(\"SET\", bucket_key .. \":last_request_time\", current_time)\n" +
                "    redis.call(\"SET\", bucket_key, water)\n" +
                "    return 1\n" +
                "else\n" +
                "    return 0\n" +
                "end\n";
        // 执行 Lua 脚本
        Object result = stringRedisTemplate.execute(
                new DefaultRedisScript(luaScript, Boolean.class),
                Collections.singletonList("rate_limiter"),
                String.valueOf(CAPACITY),
                String.valueOf(RATE),
                String.valueOf(currentTime)
        );

        // 解析结果，判断是否允许通过
        return result != null && (boolean)result == true;
    }







    //初始化可以先把BUCKETKEY放到redis中。
    @Override
    public void afterPropertiesSet() throws Exception {
        if(!redisUtils.hasKey(BUCKETKEY)){
            redisUtils.strSet(BUCKETKEY, "0");
        }
    }
}
