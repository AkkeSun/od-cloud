package com.odcloud.infrastructure.filter;

import static com.odcloud.infrastructure.util.JsonUtil.extractJsonField;
import static com.odcloud.infrastructure.util.JsonUtil.maskPassword;
import static com.odcloud.infrastructure.util.JsonUtil.toJsonBody;
import static com.odcloud.infrastructure.util.JsonUtil.toJsonParams;
import static com.odcloud.infrastructure.util.TextUtil.truncateTextLimit;

import com.odcloud.application.port.out.ApiCallLogStoragePort;
import com.odcloud.application.port.out.ApiInfoStoragePort;
import com.odcloud.domain.model.ApiCallLog;
import com.odcloud.domain.model.ApiInfo;
import com.odcloud.infrastructure.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiCallLogFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ApiInfoStoragePort apiInfoStoragePort;
    private final ApiCallLogStoragePort apiCallLogStoragePort;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        String responseBody = new String(wrappedResponse.getContentAsByteArray(),
            StandardCharsets.UTF_8);

        try {
            String errorCode = extractJsonField(responseBody, "data", "errorCode");
            String requestBody = maskPassword(toJsonBody(wrappedRequest));
            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(jwtUtil.getAccountInfo(request))
                .uri(request.getRequestURI())
                .httpMethod(request.getMethod())
                .requestParam(toJsonParams(request))
                .requestBody(truncateTextLimit(requestBody))
                .responseBody(truncateTextLimit(responseBody))
                .httpStatus(extractJsonField(responseBody, "httpStatus"))
                .errorCode(errorCode)
                .regDateTime(LocalDateTime.now())
                .build();

            ApiInfo apiInfo = apiInfoStoragePort.findByApiCallLog(apiCallLog);
            if (apiInfo != null) {
                apiCallLog.updateApiId(apiInfo.id());
                apiCallLog.updateRequestPathParam(apiInfo);
                apiCallLogStoragePort.register(apiCallLog);
            }

            // -- Filter, Interceptor Level Exception Check ---
            if (errorCode.endsWith("99")) {
                log.info(apiCallLog.getRequestLog());
                log.info(apiCallLog.getResponseLog());
            }
        } catch (Exception ignored) {
        }

        wrappedResponse.copyBodyToResponse();
    }
}
