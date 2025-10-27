package com.odcloud.infrastructure.handler;

import static com.odcloud.infrastructure.exception.ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY;
import static com.odcloud.infrastructure.util.JsonUtil.toJsonString;

import com.odcloud.infrastructure.exception.ErrorResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res,
        AuthenticationException authException) throws IOException, ServletException {
        String responseBody = toJsonString(ApiResponse.of(
            HttpStatus.UNAUTHORIZED,
            ErrorResponse.builder()
                .errorCode(INVALID_ACCESS_TOKEN_BY_SECURITY.getCode())
                .errorMessage(INVALID_ACCESS_TOKEN_BY_SECURITY.getMessage())
                .build()));
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.getWriter().write(responseBody);
    }
}
