package com.yyself.tool.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.yyself.tool.exception.JacksonException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.SerializationFeature.*;

/**
 * @author yangyu
 * @date 2020/6/18 下午10:30
 */

public class JacksonUtils {

    private static ObjectMapper mapper;

    /**
     * 是否缩进JSON格式
     */
    protected static final boolean IS_ENABLE_INDENT_OUTPUT = false;

    static {
        //初始化
        mapper = new ObjectMapper();
        //配置JSON缩进支持
        mapper.configure(INDENT_OUTPUT, IS_ENABLE_INDENT_OUTPUT);
        config(mapper);
    }

    protected static void config(ObjectMapper objectMapper) {
        //序列化BigDecimal时之间输出原始数字还是科学计数, 默认false, 即是否以toPlainString()科学计数方式来输出
        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        //允许将JSON空字符串强制转换为null对象值
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        //允许单个数值当做数组处理
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        //禁止重复键, 抛出异常
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        //禁止使用int代表Enum的order()來反序列化Enum, 抛出异常
        objectMapper.enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        //有属性不能映射的时候不报错
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //使用null表示集合类型字段是时不抛异常
        objectMapper.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        //对象为空时不抛异常
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);

        //允许在JSON中使用c/c++风格注释
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        //允许未知字段
        objectMapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
        //在JSON中允许未引用的字段名
        objectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        //时间格式
        objectMapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //识别单引号
        objectMapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
    }

    /**
     * 序列化为JSON
     */
    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JacksonException("jackson format error", e);
        }
    }

    /**
     * 序列化为JSON
     */
    public static String toJson(Object object, boolean format) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(INDENT_OUTPUT, format);
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JacksonException("jackson format error", e);
        }
    }

    /**
     * JSON反序列化（Map）
     */
    public static Map<String, Object> toMap(Object object) {
        if (object == null) {
            return null;
        }
        String json = object instanceof String ? (String) object : toJson(object);
        MapType mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
        try {
            return mapper.readValue(json, mapType);
        } catch (IOException e) {
            throw new JacksonException("jackson format error," + json, e);
        }
    }

    /**
     * JSON反序列化
     */
    public static <V> V toBean(String json, Type type) {
        if (json == null) {
            return null;
        }
        try {
            JavaType javaType = mapper.getTypeFactory().constructType(type);
            return mapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new JacksonException("jackson format error," + json, e);
        }
    }

    public static <V> List<V> toList(String json) {
        if (json == null) {
            return null;
        }
        try {
            JavaType javaType = mapper.getTypeFactory().constructType(List.class);
            return mapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new JacksonException("jackson format error," + json, e);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class User {
        private Long id;
        private String name;
        private String gender;
        private Date createTime;
        private Double score;
        private String password;
    }

    public static void main(String[] args) {
        String str = "{\"orderNo\":\"480068205904560128\",\"riskNo\":\"b26cf2b162a546918e\",\"param\":{\"cellPhone\":\"082144169349\"},\"appId\":\"payday_uku\",\"customerId\":314878612385869824,\"businessId\":\"uku\",\"collection\":\"tdFullVersion\",\"collectionFlow\":\"tdFullVersionFlow\"}";

        String json = toJson(new User(418491930660503552L, "张三", "M", new Date(), 99.8D, null));
        System.out.println("toJson => " + json);

        System.out.println("toMap => " + toMap(str).get("customerId").getClass());

        List<User> list = toList("[" + json + "]");
        System.out.println("toList => " + list);

        List<Map<String, Object>> maps = toList("[" + json + "]");
        System.out.println("toMaps => " + maps);

        User user = toBean(json, User.class);
        System.out.println("toBean => " + user);

    }


}
