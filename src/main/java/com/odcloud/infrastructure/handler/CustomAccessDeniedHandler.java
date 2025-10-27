package com.odcloud.infrastructure.handler;

import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED_BY_SECURITY;
import static com.odcloud.infrastructure.util.JsonUtil.toJsonString;

import com.odcloud.infrastructure.exception.ErrorResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String responseBody = toJsonString(ApiResponse.of(
            HttpStatus.FORBIDDEN,
            ErrorResponse.builder()
                .errorCode(ACCESS_DENIED_BY_SECURITY.getCode())
                .errorMessage(ACCESS_DENIED_BY_SECURITY.getMessage())
                .build()));
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.getWriter().write(responseBody);
    }
}
