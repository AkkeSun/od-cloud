package com.odcloud.infrastructure.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.web.util.ContentCachingRequestWrapper;

public class JsonUtil {

    public final static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static <T> T parseJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseJsonList(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectNode toObjectNode(Object obj) {
        return objectMapper.valueToTree(obj);
    }

    public static String extractJsonField(String json, String... path) {
        try {
            JsonNode node = objectMapper.readTree(json);
            for (String key : path) {
                node = node.path(key);
            }
            return node.isMissingNode() ? "" : node.asText();
        } catch (Exception e) {
            return "";
        }
    }

    public static String toJsonParams(HttpServletRequest request) {
        ObjectNode requestParam = objectMapper.createObjectNode();
        request.getParameterMap().forEach((key, value) -> {
            requestParam.put(key, String.join(",", value));
        });
        return requestParam.toString();
    }

    public static String toJsonBody(ContentCachingRequestWrapper wrappedRequest) {
        try {
            String requestBodyStr = new String(wrappedRequest.getContentAsByteArray(),
                StandardCharsets.UTF_8);
            JsonNode bodyNode = objectMapper.readTree(requestBodyStr);
            return bodyNode.toString().isEmpty() ? "{}" : bodyNode.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    public static String maskPassword(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.isObject() && root.has("password")) {
                ((ObjectNode) root).put("password", "SECRET");
            }
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            return json;
        }
    }
}