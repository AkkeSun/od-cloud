package com.odcloud.infrastructure.response;

import static com.odcloud.infrastructure.util.JsonUtil.objectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private int httpStatus;
    private String message;
    private T data;

    public ApiResponse(HttpStatus httpStatus, String message, T data) {
        this.httpStatus = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
        return new ApiResponse<>(httpStatus, message, data);
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, T data) {
        return new ApiResponse<>(httpStatus, httpStatus.name(), data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(HttpStatus.OK, data);
    }

    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
