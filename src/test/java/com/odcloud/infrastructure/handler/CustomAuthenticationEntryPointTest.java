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
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("[commence] 인증 실패 처리")
    class Describe_commence {

        @Test
        @DisplayName("[success] 인증 실패 시 401 응답과 에러 메시지를 반환한다")
        void success() throws ServletException, IOException {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            given(response.getWriter()).willReturn(writer);

            // when
            customAuthenticationEntryPoint.commence(request, response, authException);

            // then
            verify(response, times(1)).setCharacterEncoding("UTF-8");
            verify(response, times(1)).setContentType("application/json");
            verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());

            writer.flush();
            String responseBody = stringWriter.toString();
            assertThat(responseBody).isNotEmpty();

            ApiResponse<?> apiResponse = objectMapper.readValue(responseBody, ApiResponse.class);
            assertThat(apiResponse.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);

            String dataJson = objectMapper.writeValueAsString(apiResponse.getData());
            ErrorResponse errorResponse = objectMapper.readValue(dataJson, ErrorResponse.class);
            assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY.getCode());
            assertThat(errorResponse.getErrorMessage()).isEqualTo(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY.getMessage());
        }

        @Test
        @DisplayName("[success] 응답 본문에 올바른 JSON 형식의 에러 정보를 포함한다")
        void success_jsonFormat() throws ServletException, IOException {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            given(response.getWriter()).willReturn(writer);

            // when
            customAuthenticationEntryPoint.commence(request, response, authException);

            // then
            writer.flush();
            String responseBody = stringWriter.toString();

            assertThat(responseBody).contains("httpStatus");
            assertThat(responseBody).contains("UNAUTHORIZED");
            assertThat(responseBody).contains("errorCode");
            assertThat(responseBody).contains("2099");
            assertThat(responseBody).contains("errorMessage");
            assertThat(responseBody).contains("유효한 인증 토큰이 아닙니다");
        }

        @Test
        @DisplayName("[success] UTF-8 인코딩을 설정한다")
        void success_utf8Encoding() throws ServletException, IOException {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            given(response.getWriter()).willReturn(writer);

            // when
            customAuthenticationEntryPoint.commence(request, response, authException);

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
            customAuthenticationEntryPoint.commence(request, response, authException);

            // then
            verify(response, times(1)).setContentType("application/json");
        }

        @Test
        @DisplayName("[success] HTTP 상태 코드를 401로 설정한다")
        void success_httpStatus() throws ServletException, IOException {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            given(response.getWriter()).willReturn(writer);

            // when
            customAuthenticationEntryPoint.commence(request, response, authException);

            // then
            verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
            verify(response, times(1)).setStatus(401);
        }

        @Test
        @DisplayName("[success] 다양한 AuthenticationException에 대해 동일한 응답을 반환한다")
        void success_variousAuthenticationExceptions() throws ServletException, IOException {
            // given
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            given(response.getWriter()).willReturn(writer);

            org.springframework.security.authentication.BadCredentialsException badCredentialsException =
                new org.springframework.security.authentication.BadCredentialsException("Bad credentials");

            // when
            customAuthenticationEntryPoint.commence(request, response, badCredentialsException);

            // then
            writer.flush();
            String responseBody = stringWriter.toString();

            ApiResponse<?> apiResponse = objectMapper.readValue(responseBody, ApiResponse.class);
            assertThat(apiResponse.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);

            String dataJson = objectMapper.writeValueAsString(apiResponse.getData());
            ErrorResponse errorResponse = objectMapper.readValue(dataJson, ErrorResponse.class);
            assertThat(errorResponse.getErrorCode()).isEqualTo(2099);
        }
    }
}
