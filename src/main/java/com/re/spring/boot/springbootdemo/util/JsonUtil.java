package com.re.spring.boot.springbootdemo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 设置json反序列化，支持LocalDateTime中"yyyy-MM-dd HH:mm:ss"
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static <T> T j2b(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }

    public static <T> List<T> j2l(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, new ObjectMapper().getTypeFactory().constructParametricType(List.class, clazz));
    }

    public static String b2j(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
