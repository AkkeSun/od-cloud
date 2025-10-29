package com.odcloud.infrastructure.util;

import static com.odcloud.infrastructure.util.JsonUtil.objectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ToStringUtil {

    public static String toString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
