package com.odcloud.infrastructure.filter;

import static com.odcloud.infrastructure.util.JsonUtil.extractJsonField;
import static com.odcloud.infrastructure.util.JsonUtil.maskPassword;
import static com.odcloud.infrastructure.util.JsonUtil.toJsonBody;
import static com.odcloud.infrastructure.util.JsonUtil.toJsonParams;
import static com.odcloud.infrastructure.util.JsonUtil.toJsonString;
import static com.odcloud.infrastructure.util.TextUtil.truncateTextLimit;

import com.odcloud.application.port.out.ApiCallLogStoragePort;
import com.odcloud.application.port.out.ApiInfoStoragePort;
import com.odcloud.application.port.out.RedisStoragePort;
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
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiCallLogFilter extends OncePerRequestFilter {

    private static final String API_INFO_CACHE_KEY = "api:info:all";

    private final JwtUtil jwtUtil;
    private final ApiInfoStoragePort apiInfoStoragePort;
    private final ApiCallLogStoragePort apiCallLogStoragePort;
    private final RedisStoragePort redisStoragePort;
    private final AntPathMatcher matcher = new AntPathMatcher();

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
                .regDt(LocalDateTime.now())
                .build();

            ApiInfo apiInfo = findApiInfoWithCache(apiCallLog);
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

    private ApiInfo findApiInfoWithCache(ApiCallLog apiCallLog) {
        // 1. Redis 캐시 목록 조회
        List<ApiInfo> cachedApiInfos = redisStoragePort.findDataList(API_INFO_CACHE_KEY,
            ApiInfo.class);

        if (cachedApiInfos.isEmpty()) {
            // 2-1. 캐시 목록이 없다면 DB 조회 후 캐시 갱신
            log.debug("ApiInfo 캐시 미스: DB에서 조회");
            return refreshCacheAndFind(apiCallLog);
        }

        // 2-2. 캐시 목록이 있다면 일치하는 정보 확인
        log.debug("ApiInfo 캐시 히트: Redis에서 {} 건 조회", cachedApiInfos.size());
        ApiInfo matchedInfo = findMatchingApiInfo(cachedApiInfos, apiCallLog);

        if (matchedInfo != null) {
            // 일치하는 정보가 있으면 반환
            return matchedInfo;
        }

        // 2-2-2. 캐시에 없는 API라면 DB 조회 후 캐시 갱신
        log.debug("ApiInfo 캐시에서 매칭 실패: DB 재조회 및 캐시 갱신");
        return refreshCacheAndFind(apiCallLog);
    }

    private ApiInfo refreshCacheAndFind(ApiCallLog apiCallLog) {
        // DB에서 최신 목록 조회
        List<ApiInfo> apiInfos = apiInfoStoragePort.findAll();

        // Redis 캐시 갱신
        if (!apiInfos.isEmpty()) {
            redisStoragePort.register(API_INFO_CACHE_KEY, toJsonString(apiInfos));
            log.debug("ApiInfo 캐시 갱신: Redis에 {} 건 저장", apiInfos.size());
        }

        // 일치하는 정보 찾아서 반환
        return findMatchingApiInfo(apiInfos, apiCallLog);
    }

    private ApiInfo findMatchingApiInfo(List<ApiInfo> apiInfos, ApiCallLog apiCallLog) {
        return apiInfos.stream()
            .filter(api -> Objects.equals(api.httpMethod(), apiCallLog.getHttpMethod()))
            .filter(api -> matcher.match(api.uriPattern(), apiCallLog.getUri()))
            .findFirst()
            .orElse(null);
    }
}
