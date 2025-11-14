package com.odcloud.infrastructure.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.infrastructure.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ExceptionAdviceTest {

    private ExceptionAdvice exceptionAdvice;

    @BeforeEach
    void setUp() {
        exceptionAdvice = new ExceptionAdvice();
    }

    @Nested
    @DisplayName("[bindException] BindException 처리")
    class Describe_bindException {

        @Test
        @DisplayName("[success] BindException을 처리하여 400 응답을 반환한다")
        void success() {
            // given
            BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
            FieldError fieldError = new FieldError("object", "field", "필드 검증 실패");
            org.mockito.BDDMockito.given(bindingResult.getAllErrors())
                .willReturn(java.util.List.of(fieldError));

            BindException exception = new BindException(bindingResult);

            // when
            ApiResponse<Object> response = exceptionAdvice.bindException(exception);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getData()).isInstanceOf(ErrorResponse.class);
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(1099);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("필드 검증 실패");
        }

        @Test
        @DisplayName("[success] 여러 검증 오류 중 첫 번째 오류를 반환한다")
        void success_multipleErrors() {
            // given
            BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
            FieldError fieldError1 = new FieldError("object", "field1", "첫 번째 오류");
            FieldError fieldError2 = new FieldError("object", "field2", "두 번째 오류");
            org.mockito.BDDMockito.given(bindingResult.getAllErrors())
                .willReturn(java.util.List.of(fieldError1, fieldError2));

            BindException exception = new BindException(bindingResult);

            // when
            ApiResponse<Object> response = exceptionAdvice.bindException(exception);

            // then
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorMessage()).isEqualTo("첫 번째 오류");
        }
    }

    @Nested
    @DisplayName("[MethodArgumentNotValidException] MethodArgumentNotValidException 처리")
    class Describe_MethodArgumentNotValidException {

        @Test
        @DisplayName("[success] MethodArgumentNotValidException을 처리하여 400 응답을 반환한다")
        void success() {
            // given
            BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
            FieldError fieldError = new FieldError("object", "field", "인자 검증 실패");
            org.mockito.BDDMockito.given(bindingResult.getAllErrors())
                .willReturn(java.util.List.of(fieldError));

            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                null, bindingResult);

            // when
            ApiResponse<Object> response = exceptionAdvice.MethodArgumentNotValidException(exception);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getData()).isInstanceOf(ErrorResponse.class);
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(1099);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("인자 검증 실패");
        }
    }

    @Nested
    @DisplayName("[customAuthenticationException] CustomAuthenticationException 처리")
    class Describe_customAuthenticationException {

        @Test
        @DisplayName("[success] CustomAuthenticationException을 처리하여 401 응답을 반환한다")
        void success() {
            // given
            CustomAuthenticationException exception = new CustomAuthenticationException(
                ErrorCode.INVALID_ACCESS_TOKEN);

            // when
            ApiResponse<Object> response = exceptionAdvice.customAuthenticationException(exception);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getData()).isInstanceOf(ErrorResponse.class);
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(2001);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("유효한 인증 토큰이 아닙니다");
        }

        @Test
        @DisplayName("[success] INVALID_REFRESH_TOKEN 예외를 처리한다")
        void success_invalidRefreshToken() {
            // given
            CustomAuthenticationException exception = new CustomAuthenticationException(
                ErrorCode.INVALID_REFRESH_TOKEN);

            // when
            ApiResponse<Object> response = exceptionAdvice.customAuthenticationException(exception);

            // then
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(2002);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("유효한 리프레시 토큰이 아닙니다");
        }

        @Test
        @DisplayName("[success] INVALID_GOOGLE_TOKEN 예외를 처리한다")
        void success_invalidGoogleToken() {
            // given
            CustomAuthenticationException exception = new CustomAuthenticationException(
                ErrorCode.INVALID_GOOGLE_TOKEN);

            // when
            ApiResponse<Object> response = exceptionAdvice.customAuthenticationException(exception);

            // then
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(2003);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("유효한 구글 토큰이 아닙니다");
        }
    }

    @Nested
    @DisplayName("[customAuthorizationException] CustomAuthorizationException 처리")
    class Describe_customAuthorizationException {

        @Test
        @DisplayName("[success] CustomAuthorizationException을 처리하여 403 응답을 반환한다")
        void success() {
            // given
            CustomAuthorizationException exception = new CustomAuthorizationException(
                ErrorCode.ACCESS_DENIED);

            // when
            ApiResponse<Object> response = exceptionAdvice.customAuthorizationException(exception);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getData()).isInstanceOf(ErrorResponse.class);
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(3001);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("접근권한이 없습니다");
        }
    }

    @Nested
    @DisplayName("[customBusinessException] CustomBusinessException 처리")
    class Describe_customBusinessException {

        @Test
        @DisplayName("[success] CustomBusinessException을 처리하여 500 응답을 반환한다")
        void success() {
            // given
            CustomBusinessException exception = new CustomBusinessException(
                ErrorCode.Business_NOT_FOUND_ACCOUNT);

            // when
            ApiResponse<Object> response = exceptionAdvice.customBusinessException(exception);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getData()).isInstanceOf(ErrorResponse.class);
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(4003);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("조회된 사용자 정보가 없습니다");
        }

        @Test
        @DisplayName("[success] SEND_EMAIL_ERROR 예외를 처리한다")
        void success_sendEmailError() {
            // given
            CustomBusinessException exception = new CustomBusinessException(
                ErrorCode.Business_SEND_EMAIL_ERROR);

            // when
            ApiResponse<Object> response = exceptionAdvice.customBusinessException(exception);

            // then
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(4001);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("이메일 전송에 실패했습니다");
        }

        @Test
        @DisplayName("[success] SAVED_USER 예외를 처리한다")
        void success_savedUser() {
            // given
            CustomBusinessException exception = new CustomBusinessException(
                ErrorCode.Business_SAVED_USER);

            // when
            ApiResponse<Object> response = exceptionAdvice.customBusinessException(exception);

            // then
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(4002);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("등록된 사용자 정보 입니다");
        }

        @Test
        @DisplayName("[success] FILE_UPLOAD_ERROR 예외를 처리한다")
        void success_fileUploadError() {
            // given
            CustomBusinessException exception = new CustomBusinessException(
                ErrorCode.Business_FILE_UPLOAD_ERROR);

            // when
            ApiResponse<Object> response = exceptionAdvice.customBusinessException(exception);

            // then
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(4011);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("파일 업로드중 오류가 발생했습니다");
        }
    }

    @Nested
    @DisplayName("[notFoundException] Exception 처리")
    class Describe_notFoundException {

        @Test
        @DisplayName("[success] 일반 Exception을 처리하여 500 응답을 반환한다")
        void success() {
            // given
            Exception exception = new Exception("예상하지 못한 오류가 발생했습니다");

            // when
            ApiResponse<Object> response = exceptionAdvice.notFoundException(exception);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getData()).isInstanceOf(ErrorResponse.class);
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(500);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("예상하지 못한 오류가 발생했습니다");
        }

        @Test
        @DisplayName("[success] RuntimeException을 처리한다")
        void success_runtimeException() {
            // given
            RuntimeException exception = new RuntimeException("런타임 오류");

            // when
            ApiResponse<Object> response = exceptionAdvice.notFoundException(exception);

            // then
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(500);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("런타임 오류");
        }

        @Test
        @DisplayName("[success] NullPointerException을 처리한다")
        void success_nullPointerException() {
            // given
            NullPointerException exception = new NullPointerException("Null 참조");

            // when
            ApiResponse<Object> response = exceptionAdvice.notFoundException(exception);

            // then
            ErrorResponse errorResponse = (ErrorResponse) response.getData();
            assertThat(errorResponse.getErrorCode()).isEqualTo(500);
            assertThat(errorResponse.getErrorMessage()).isEqualTo("Null 참조");
        }
    }
}
