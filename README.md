# ksc-internship
金山云实习做的一些项目：
第一周开发boot-jar-plugin项目：

一、编写⼀个插件构建项⽬zip包。
• 插件⽣成的zip包路径位于项⽬根⽬录下的target⽂件夹
• 插件必须基于maven⽣命中周期的compile阶段⽣成的target/classes才能完成⼯作
• zip⽂件中必须包含可执⾏的jar包，以及程序运⾏依赖的第三jar包lib⽬录
涉及的知识点
• Java可执⾏Jar包的构建与运⾏(class加载机制)
• Maven插件编写、构建、绑定、打包等
• Java读写⽂件以及打包相关API，如ZipOutputStream、JarOutputStream、Manifest等


二、运用泛型、反射、集合类等相关知识实现一个API参数解析器：项目param-parse-util
假如有如下字符串
ApiVersion=betav2
AutoCreated=null
Container.2.Command.1=/bin/bash
Container.2.Command.2=-c
Container.2.Command.3=sleep 20
Container.2.Name=nginx
Container.2.Port=8080
Container.5.Environment.1.Key=PORT
Container.5.Environment.1.Value=3306
Container.5.Environment.2.Key=ROOT_PASSWORD
Container.5.Environment.2.Value=123456
Container.5.ImagePullPolicy=IfNotPresent
Container.5.Name=mysql
Container.5.Port=8306
Memory=4.0
Metadata.Generation=1
Metadata.Name=nginx-deployment-1344556.2345
cpu=2
上述参数通过&拼接成http的queryString格式
String queryString="Cpu=2&Memory=4.0&AutoCreated=null&Container.1.Name=nginx&Container.1.Command.1=/bin/sh"
根据规则将上述queryString字符串自动映射到对象的字段上，对象中的属性只提供了get方法，不提供set方法，请通过反射设置对应的属性值，Pod对象默认提供了无参构造方法。
要求：
1. 字段Class类型可以是八种原始类型以及对应的包装类、String、BigDecimal以及自定义Object，列表参数只需使用ArrayList实现即可，无须支持其它Array、Map等数据结构。
2. queryString中的键key都是大写开头，对象中的属性必须是小写字母开头符合规范，需自动适配，如果参数中的键值key是小写字符开头，请自动忽略，如参数中有cpu=2，此时不应该将该值赋给对象中属性cpu，必须要求参数key都是大写的即Cpu=2才进行适配。
3. 通过自定义注解 SkipMappingValueAnnotation标注在属性上，工具类则不处理该属性，不去覆盖对象默认值，这个注解在后面以定义好，请勿修改类名以及包名，直接复制到工程中即可。
    @SkipMappingValueAnnotation
    private String apiVersion = "v1";
4. 当发现对应的value是null或者字符串"NULL"，"null"、"Null"时，能正确处理原始类型默认值，此时如果参数中传递了AutoCreated=null字符串，需自动将对象中的autoCreated设置为false。
private boolean autoCreated = true;
5. 当属性字段是一个列表类型时，请按照N进行排序，此时如下应该生成2个对象的列表List<Container>，赋值给pod对象中的container字段，Container.1.Name中间的数字没有实际意义，只是用来决定其在列表中的顺序，数字越小，在列表中的下标越小，如下 nginx对应的对象是列表中第一个对象，下标为0，紧随其后的是redis。
// 解析后的container列表中第一个元素必须是nginx，如果N重复，最终只保留一个值
Container.1.Name=nginx
Container.2.Name=redis

三、trade-order-service
需求：完成基础的Java工程化项目，实现相关接口，考虑部分容错机制。
应关注的核心问题- 缓存- 负载均衡- 限流- 超时，重试，幂等。
需要实现的功能：
    1.实现类Nginx请求转发功能
自动化测试的请求只会打到trade-order-gateway，需在trade-order-gateway上实现负载均衡算法并转发请求至后端trade-order-api。具体业务功能实现在trade-order-api模块上。
    2.查询订单详情
MYSQL库及表：test_trade: ksc_trade_order | ksc_trade_product_config
订单包含订单信息(200w) | 订单配置信息(200w) | 用户信息(20w) | 机房信息(100)
接口说明：
用户查询接口: /online/user/{id},  本接口响应时间强制控制在2s以上  
机房查询接口: /online/region/list,  本接口响应时间强制控制在4s以上，可以使用异步查询+redis缓存。
    3.根据机房Id查询机房名称
基于三方接口查询机房名称，并对外暴露API。
要实现的对外暴露API的结构请求上下文，该接口不稳定，必须采用重试机制。
    4.订单优惠券抵扣公摊
备注: URL开头/online/voucher/deduct,  以文档为准，一个订单的金额(price)可使用多张优惠券。基本业务逻辑，使用redis缓存即可。
    5.基于Redis实现漏桶限流算法，并在API调用上体现
QPS：5　（每秒支持5个请求），限流算法：漏桶
限流组件：Redis，不借助第三方框架来实现，自己的实现思路及过程可以写在readme.md上（可以在readme.md上辅助图片，自己测试的过程截图等）。这个接口只在gateway模块实现即可。
实习方式：漏桶算法加redis缓存加lua脚本。
    6.简单的链路跟踪实现
traceId用于标识某一次具体的请求ID，通过它可以将一次用户请求在系统中调用的路径串联起来，批改作业的时候header中会固定传递X-KSY-REQUEST-ID
要求：X-KSY-REQUEST-ID(注意客户端传递的是文档描述的这个关键字)体现在接口响应的reqeustId上，同时在日志调用链中体现。




