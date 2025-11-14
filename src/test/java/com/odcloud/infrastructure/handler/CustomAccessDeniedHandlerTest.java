package com.odcloud.infrastructure.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odcloud.infrastructure.exception.ErrorCode;
import com.odcloud.infrastructure.exception.ErrorResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AccessDeniedException accessDeniedException;

    @InjectMocks
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("[handle] 접근 거부 처리")
    class Describe_handle {

        @Test
        @DisplayName("[success] 접근 거부 시 403 응답과 에러 메시지를 반환한다")
        void success() throws ServletException, IOException {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            given(response.getWriter()).willReturn(writer);

            // when
            customAccessDeniedHandler.handle(request, response, accessDeniedException);

            // then
            verify(response, times(1)).setCharacterEncoding("UTF-8");
            verify(response, times(1)).setContentType("application/json");
            verify(response, times(1)).setStatus(HttpStatus.FORBIDDEN.value());

            writer.flush();
            String responseBody = stringWriter.toString();
            assertThat(responseBody).isNotEmpty();

            ApiResponse<?> apiResponse = objectMapper.readValue(responseBody, ApiResponse.class);
            assertThat(apiResponse.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);

            String dataJson = objectMapper.writeValueAsString(apiResponse.getData());
            ErrorResponse errorResponse = objectMapper.readValue(dataJson, ErrorResponse.class);
            assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED_BY_SECURITY.getCode());
            assertThat(errorResponse.getErrorMessage()).isEqualTo(ErrorCode.ACCESS_DENIED_BY_SECURITY.getMessage());
        }

        @Test
        @DisplayName("[success] 응답 본문에 올바른 JSON 형식의 에러 정보를 포함한다")
        void success_jsonFormat() throws ServletException, IOException {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            given(response.getWriter()).willReturn(writer);

            // when
            customAccessDeniedHandler.handle(request, response, accessDeniedException);

            // then
            writer.flush();
            String responseBody = stringWriter.toString();

            assertThat(responseBody).contains("httpStatus");
            assertThat(responseBody).contains("FORBIDDEN");
            assertThat(responseBody).contains("errorCode");
            assertThat(responseBody).contains("3099");
            assertThat(responseBody).contains("errorMessage");
            assertThat(responseBody).contains("접근권한이 없습니다");
        }

        @Test
        @DisplayName("[success] UTF-8 인코딩을 설정한다")
        void success_utf8Encoding() throws ServletException, IOException {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            given(response.getWriter()).willReturn(writer);

            // when
            customAccessDeniedHandler.handle(request, response, accessDeniedException);

            // then
            verify(response, times(1)).setCharacterEncoding("UTF-8");
        }

        @Test
        @DisplayName("[success] Content-Type을 application/json으로 설정한다")
        void success_contentType() throws ServletException, IOException {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            given(response.getWriter()).willReturn(writer);

            // when
            customAccessDeniedHandler.handle(request, response, accessDeniedException);

            // then
            verify(response, times(1)).setContentType("application/json");
        }

        @Test
        @DisplayName("[success] HTTP 상태 코드를 403으로 설정한다")
        void success_httpStatus() throws ServletException, IOException {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            given(response.getWriter()).willReturn(writer);

            // when
            customAccessDeniedHandler.handle(request, response, accessDeniedException);

            // then
            verify(response, times(1)).setStatus(HttpStatus.FORBIDDEN.value());
            verify(response, times(1)).setStatus(403);
        }

        @Test
        @DisplayName("[success] AccessDeniedException 메시지와 관계없이 동일한 응답을 반환한다")
        void success_sameResponseRegardlessOfExceptionMessage() throws ServletException, IOException {
            // given
            StringWriter stringWriter1 = new StringWriter();
            PrintWriter writer1 = new PrintWriter(stringWriter1);
            given(response.getWriter()).willReturn(writer1);

            AccessDeniedException exception1 = new AccessDeniedException("Custom message 1");

            // when
            customAccessDeniedHandler.handle(request, response, exception1);

            // then
            writer1.flush();
            String responseBody1 = stringWriter1.toString();

            ApiResponse<?> apiResponse = objectMapper.readValue(responseBody1, ApiResponse.class);
            assertThat(apiResponse.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);

            String dataJson = objectMapper.writeValueAsString(apiResponse.getData());
            ErrorResponse errorResponse = objectMapper.readValue(dataJson, ErrorResponse.class);
            assertThat(errorResponse.getErrorCode()).isEqualTo(3099);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("접근권한이 없습니다");
        }

        @Test
        @DisplayName("[success] 권한이 없는 리소스 접근 시도를 처리한다")
        void success_unauthorizedResourceAccess() throws ServletException, IOException {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            given(response.getWriter()).willReturn(writer);

            AccessDeniedException exception = new AccessDeniedException("User does not have required role");

            // when
            customAccessDeniedHandler.handle(request, response, exception);

            // then
            writer.flush();
            String responseBody = stringWriter.toString();

            assertThat(responseBody).contains("FORBIDDEN");
            assertThat(responseBody).contains("3099");

            verify(response, times(1)).setStatus(403);
        }

        @Test
        @DisplayName("[success] 다양한 AccessDeniedException에 대해 일관된 응답을 반환한다")
        void success_consistentResponseForVariousExceptions() throws ServletException, IOException {
            // given
            AccessDeniedException[] exceptions = {
                new AccessDeniedException("Insufficient permissions"),
                new AccessDeniedException("Role not found"),
                new AccessDeniedException("Access to resource denied")
            };

            for (AccessDeniedException exception : exceptions) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter writer = new PrintWriter(stringWriter);
                given(response.getWriter()).willReturn(writer);

                // when
                customAccessDeniedHandler.handle(request, response, exception);

                // then
                writer.flush();
                String responseBody = stringWriter.toString();

                ApiResponse<?> apiResponse = objectMapper.readValue(responseBody, ApiResponse.class);
                assertThat(apiResponse.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);

                String dataJson = objectMapper.writeValueAsString(apiResponse.getData());
                ErrorResponse errorResponse = objectMapper.readValue(dataJson, ErrorResponse.class);
                assertThat(errorResponse.getErrorCode()).isEqualTo(3099);
                assertThat(errorResponse.getErrorMessage()).isEqualTo("접근권한이 없습니다");
            }
        }
    }
}
