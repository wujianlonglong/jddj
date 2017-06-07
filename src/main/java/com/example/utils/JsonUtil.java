package com.example.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by gaoqichao on 16-6-30.
 */
public class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtil() {

    }

    /**
     * 将对象转换为json串
     *
     * @param object 实体对象
     * @return json串
     */
    public static String objectToString(Object object) {
        if (null == object) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("将对象转换为json报错", e);
        }

        return null;
    }

    /**
     * 将json串转换为指定的对象
     *
     * @param jsonStr   json串
     * @param classType 对象类型
     * @param <T>
     * @return 指定类型的对象
     */
    public static <T> T jsonToObject(String jsonStr, Class<T> classType) {
        if (StringUtils.isEmpty(jsonStr)) {
            return null;
        }

        try {
            return objectMapper.readValue(jsonStr, classType);
        } catch (IOException e) {
            log.error(String.format("字符串%s转换成对象报错:", jsonStr), e);
        }

        return null;
    }
}
