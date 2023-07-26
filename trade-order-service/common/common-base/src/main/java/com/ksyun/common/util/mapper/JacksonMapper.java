package com.ksyun.common.util.mapper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;
import java.util.TimeZone;

public class JacksonMapper {

    private static final Logger logger = LoggerFactory.getLogger(JacksonMapper.class);

    public static final JacksonMapper INSTANCE = new JacksonMapper(Include.NON_NULL);

    private ObjectMapper mapper;

    public JacksonMapper() {
        this(null);
    }

    public JacksonMapper(Include include) {
        mapper = new ObjectMapper();
        // 设置输出时包含属性的风格
        if (include != null) {
            mapper.setSerializationInclusion(include);
        }
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    }

    /**
     * 创建只输出非Null的属性到Json字符串的Mapper.
     */
    public static JacksonMapper nonNullMapper() {
        return new JacksonMapper(Include.NON_NULL);
    }

    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper.
     * <p>
     * 注意，要小心使用, 特别留意empty的情况.
     */
    public static JacksonMapper nonEmptyMapper() {
        return new JacksonMapper(Include.NON_EMPTY);
    }

    public static JacksonMapper nonDefaultMapper() {
        return new JacksonMapper(Include.NON_DEFAULT);
    }

    /**
     * 默认的全部输出的Mapper, 区别于INSTANCE，可以做进一步的配置
     */
    public static JacksonMapper defaultMapper() {
        return new JacksonMapper();
    }

    /**
     * Object可以是POJO，也可以是Collection或数组。 如果对象为Null, 返回"null". 如果集合为空集合, 返回"[]".
     */
    public String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("write to json string error:" + object, e);
        }
    }

    public String toPrettyJson(Object object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("write to pretty json string error:" + object, e);
        }
    }

    public String toPrettyJson(String str) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(str));
        } catch (IOException e) {
            throw new RuntimeException("write to pretty json string error:" + str, e);
        }
    }

    public boolean isJSONValid(String str) {
        try {
            JsonNode jsonNode = mapper.readTree(str);
            JsonNodeType jsonNodeType = jsonNode.getNodeType();
            return jsonNodeType == JsonNodeType.OBJECT || jsonNodeType == JsonNodeType.ARRAY;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 反序列化POJO或简单Collection如List<String>.
     * <p>
     * 如果JSON字符串为Null或"null"字符串, 返回Null. 如果JSON字符串为"[]", 返回空集合.
     * <p>
     * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String, JavaType)
     *
     * @see #fromJson(String, JavaType)
     */
    public <T> T fromJson( String jsonString, Class<T> clazz) {
        Validate.notEmpty(jsonString);
        logger.debug("jsonString:{}",jsonString);
        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            throw new RuntimeException("parse json string error:" + jsonString, e);
        }
    }

    /**
     * 反序列化复杂Collection如List<Bean>, contructCollectionType()或contructMapType()构造类型, 然后调用本函数.
     */
    public <T> T fromJson( String jsonString, JavaType javaType) {
        Validate.notEmpty(jsonString);
        try {
            return (T) mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            throw new RuntimeException("parse json string error:" + jsonString, e);
        }
    }

    public <T> T fromJson(String jsonString, TypeReference valueTypeRef) {
        if (jsonString==null || jsonString.trim().length()==0) {
            return null;
        }
        try {
            return (T) mapper.readValue(jsonString, valueTypeRef);
        } catch (IOException e) {
            logger.error("parse json string error:" + jsonString, e);
            return null;
        }
    }

    /**
     * 构造Collection类型.
     */
    public JavaType buildCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    /**
     * 构造Map类型.
     */
    public JavaType buildMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    /**
     * 当JSON里只含有Bean的部分属性時，更新一个已存在Bean，只覆盖该部分的属性.
     */
    public void update(String jsonString, Object object) {
        try {
            mapper.readerForUpdating(object).readValue(jsonString);
        } catch (IOException e) {
            logger.error("update json string:" + jsonString + " to object:" + object + " error.", e);
        }
    }

    /**
     * 输出JSONP格式數據.
     */
    public String toJsonP(String functionName, Object object) {
        return toJson(new JSONPObject(functionName, object));
    }

    /**
     * 设定是否使用Enum的toString函数来读定Enum, 为False时使用Enum的name()函数来读写Enum, 默认为False. 注意本函数一定要在Mapper创建后, 所有的读写动作之前调用.
     */
    public void enableEnumUseToString() {
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    /**
     * 取出Mapper做进一步的设置或使用其他序列化API.
     */
    public ObjectMapper getMapper() {
        return mapper;
    }

    public JacksonMapper disable(SerializationFeature configFeature) {
        mapper.disable(configFeature);
        return this;
    }

    public JacksonMapper setDateFormat(String datePattern) {
        mapper.setDateFormat(new SimpleDateFormat(datePattern));
        return this;
    }
}
