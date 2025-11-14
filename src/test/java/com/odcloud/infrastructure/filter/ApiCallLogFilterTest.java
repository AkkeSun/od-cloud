package com.odcloud.infrastructure.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@ExtendWith(MockitoExtension.class)
class ApiCallLogFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ApiInfoStoragePort apiInfoStoragePort;

    @Mock
    private ApiCallLogStoragePort apiCallLogStoragePort;

    @Mock
    private RedisStoragePort redisStoragePort;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private ApiCallLogFilter apiCallLogFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("[doFilterInternal] API 호출 로그 필터 처리")
    class Describe_doFilterInternal {

        @Test
        @DisplayName("[success] 정상적인 API 호출을 로깅한다")
        void success() throws ServletException, IOException {
            // given
            HttpServletRequest originalRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
            HttpServletResponse originalResponse = org.mockito.Mockito.mock(HttpServletResponse.class);

            given(originalRequest.getRequestURI()).willReturn("/api/test");
            given(originalRequest.getMethod()).willReturn("GET");

            Map<String, String[]> paramMap = new HashMap<>();
            paramMap.put("param1", new String[]{"value1"});
            given(originalRequest.getParameterMap()).willReturn(paramMap);

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");
            given(jwtUtil.getAccountInfo(any())).willReturn(accountInfo);

            String responseBodyJson = "{\"httpStatus\":\"OK\",\"data\":{\"errorCode\":\"\"}}";

            ApiInfo mockApiInfo = ApiInfo.builder()
                .id(1L)
                .uri("/api/test")
                .httpMethod("GET")
                .build();

            // Redis 캐시 미스 시나리오
            given(redisStoragePort.findDataList(any(), any())).willReturn(Collections.emptyList());
            given(apiInfoStoragePort.findAll()).willReturn(List.of(mockApiInfo));

            // when
            apiCallLogFilter.doFilterInternal(originalRequest, originalResponse, new FilterChain() {
                @Override
                public void doFilter(jakarta.servlet.ServletRequest request,
                    jakarta.servlet.ServletResponse response) throws IOException, ServletException {
                    ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                    wrapper.getOutputStream().write(responseBodyJson.getBytes(StandardCharsets.UTF_8));
                    wrapper.copyBodyToResponse();
                }
            });

            // then
            verify(apiCallLogStoragePort, times(1)).register(any(ApiCallLog.class));
        }

        @Test
        @DisplayName("[success] ApiInfo가 없으면 로그를 저장하지 않는다")
        void success_noApiInfo() throws ServletException, IOException {
            // given
            HttpServletRequest originalRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
            HttpServletResponse originalResponse = org.mockito.Mockito.mock(HttpServletResponse.class);

            given(originalRequest.getRequestURI()).willReturn("/api/unknown");
            given(originalRequest.getMethod()).willReturn("GET");
            given(originalRequest.getParameterMap()).willReturn(new HashMap<>());

            ObjectNode accountInfo = objectMapper.createObjectNode();
            given(jwtUtil.getAccountInfo(any())).willReturn(accountInfo);

            // Redis 캐시 미스 및 DB에도 없는 경우
            given(redisStoragePort.findDataList(any(), any())).willReturn(Collections.emptyList());
            given(apiInfoStoragePort.findAll()).willReturn(Collections.emptyList());

            String responseBodyJson = "{\"httpStatus\":\"OK\"}";

            // when
            apiCallLogFilter.doFilterInternal(originalRequest, originalResponse, new FilterChain() {
                @Override
                public void doFilter(jakarta.servlet.ServletRequest request,
                    jakarta.servlet.ServletResponse response) throws IOException, ServletException {
                    ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                    wrapper.getOutputStream().write(responseBodyJson.getBytes(StandardCharsets.UTF_8));
                    wrapper.copyBodyToResponse();
                }
            });

            // then
            verify(apiCallLogStoragePort, never()).register(any(ApiCallLog.class));
        }

        @Test
        @DisplayName("[success] 예외가 발생해도 필터 체인은 계속된다")
        void success_exceptionInLogging() throws ServletException, IOException {
            // given
            HttpServletRequest originalRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
            HttpServletResponse originalResponse = org.mockito.Mockito.mock(HttpServletResponse.class);

            given(originalRequest.getRequestURI()).willReturn("/api/test");
            given(originalRequest.getMethod()).willReturn("GET");
            given(originalRequest.getParameterMap()).willThrow(new RuntimeException("Unexpected error"));

            String responseBodyJson = "{}";

            boolean[] filterChainCalled = {false};

            // when
            apiCallLogFilter.doFilterInternal(originalRequest, originalResponse, new FilterChain() {
                @Override
                public void doFilter(jakarta.servlet.ServletRequest request,
                    jakarta.servlet.ServletResponse response) throws IOException, ServletException {
                    filterChainCalled[0] = true;
                    ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                    wrapper.getOutputStream().write(responseBodyJson.getBytes(StandardCharsets.UTF_8));
                    wrapper.copyBodyToResponse();
                }
            });

            // then - 예외가 발생해도 필터 체인은 계속되어야 함
            assert filterChainCalled[0];
        }

        @Test
        @DisplayName("[success] POST 요청의 Body를 로깅한다")
        void success_postRequestWithBody() throws ServletException, IOException {
            // given
            HttpServletRequest originalRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
            HttpServletResponse originalResponse = org.mockito.Mockito.mock(HttpServletResponse.class);

            given(originalRequest.getRequestURI()).willReturn("/api/register");
            given(originalRequest.getMethod()).willReturn("POST");
            given(originalRequest.getParameterMap()).willReturn(new HashMap<>());

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");
            given(jwtUtil.getAccountInfo(any())).willReturn(accountInfo);

            String responseBodyJson = "{\"httpStatus\":\"CREATED\"}";

            ApiInfo mockApiInfo = ApiInfo.builder()
                .id(2L)
                .uri("/api/register")
                .httpMethod("POST")
                .build();

            // Redis 캐시 미스 시나리오
            given(redisStoragePort.findDataList(any(), any())).willReturn(Collections.emptyList());
            given(apiInfoStoragePort.findAll()).willReturn(List.of(mockApiInfo));

            // when
            apiCallLogFilter.doFilterInternal(originalRequest, originalResponse, new FilterChain() {
                @Override
                public void doFilter(jakarta.servlet.ServletRequest request,
                    jakarta.servlet.ServletResponse response) throws IOException, ServletException {
                    ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                    wrapper.getOutputStream().write(responseBodyJson.getBytes(StandardCharsets.UTF_8));
                    wrapper.copyBodyToResponse();
                }
            });

            // then
            verify(apiCallLogStoragePort, times(1)).register(any(ApiCallLog.class));
        }

        @Test
        @DisplayName("[success] password 필드가 있는 요청 Body를 마스킹한다")
        void success_maskPasswordInRequestBody() throws ServletException, IOException {
            // given
            HttpServletRequest originalRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
            HttpServletResponse originalResponse = org.mockito.Mockito.mock(HttpServletResponse.class);

            given(originalRequest.getRequestURI()).willReturn("/api/login");
            given(originalRequest.getMethod()).willReturn("POST");
            given(originalRequest.getParameterMap()).willReturn(new HashMap<>());

            ObjectNode accountInfo = objectMapper.createObjectNode();
            given(jwtUtil.getAccountInfo(any())).willReturn(accountInfo);

            String responseBodyJson = "{\"httpStatus\":\"OK\"}";

            ApiInfo mockApiInfo = ApiInfo.builder()
                .id(3L)
                .uri("/api/login")
                .httpMethod("POST")
                .build();

            // Redis 캐시 미스 시나리오
            given(redisStoragePort.findDataList(any(), any())).willReturn(Collections.emptyList());
            given(apiInfoStoragePort.findAll()).willReturn(List.of(mockApiInfo));

            // when
            apiCallLogFilter.doFilterInternal(originalRequest, originalResponse, new FilterChain() {
                @Override
                public void doFilter(jakarta.servlet.ServletRequest request,
                    jakarta.servlet.ServletResponse response) throws IOException, ServletException {
                    ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                    wrapper.getOutputStream().write(responseBodyJson.getBytes(StandardCharsets.UTF_8));
                    wrapper.copyBodyToResponse();
                }
            });

            // then
            verify(apiCallLogStoragePort, times(1)).register(any(ApiCallLog.class));
        }

        @Test
        @DisplayName("[success] 에러 응답을 로깅한다")
        void success_errorResponse() throws ServletException, IOException {
            // given
            HttpServletRequest originalRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
            HttpServletResponse originalResponse = org.mockito.Mockito.mock(HttpServletResponse.class);

            given(originalRequest.getRequestURI()).willReturn("/api/error");
            given(originalRequest.getMethod()).willReturn("GET");
            given(originalRequest.getParameterMap()).willReturn(new HashMap<>());

            ObjectNode accountInfo = objectMapper.createObjectNode();
            given(jwtUtil.getAccountInfo(any())).willReturn(accountInfo);

            String responseBodyJson = "{\"httpStatus\":\"INTERNAL_SERVER_ERROR\",\"data\":{\"errorCode\":\"2099\"}}";

            ApiInfo mockApiInfo = ApiInfo.builder()
                .id(4L)
                .uri("/api/error")
                .httpMethod("GET")
                .build();

            // Redis 캐시 미스 시나리오
            given(redisStoragePort.findDataList(any(), any())).willReturn(Collections.emptyList());
            given(apiInfoStoragePort.findAll()).willReturn(List.of(mockApiInfo));

            // when
            apiCallLogFilter.doFilterInternal(originalRequest, originalResponse, new FilterChain() {
                @Override
                public void doFilter(jakarta.servlet.ServletRequest request,
                    jakarta.servlet.ServletResponse response) throws IOException, ServletException {
                    ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                    wrapper.getOutputStream().write(responseBodyJson.getBytes(StandardCharsets.UTF_8));
                    wrapper.copyBodyToResponse();
                }
            });

            // then
            verify(apiCallLogStoragePort, times(1)).register(any(ApiCallLog.class));
        }
    }
}
