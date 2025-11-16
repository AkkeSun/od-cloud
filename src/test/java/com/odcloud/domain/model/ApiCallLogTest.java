package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ApiCallLogTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("[updateResponseBody] responseBody를 업데이트하는 메서드")
    class Describe_updateResponseBody {

        @Test
        @DisplayName("[success] responseBody를 업데이트한다")
        void success() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .responseBody(null)
                .build();

            // when
            log.updateResponseBody("{\"result\":true}");

            // then
            assertThat(log.getResponseBody()).isEqualTo("{\"result\":true}");
        }

        @Test
        @DisplayName("[success] responseBody를 null로 업데이트한다")
        void success_null() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .responseBody("{\"result\":true}")
                .build();

            // when
            log.updateResponseBody(null);

            // then
            assertThat(log.getResponseBody()).isNull();
        }

        @Test
        @DisplayName("[success] responseBody를 빈 문자열로 업데이트한다")
        void success_emptyString() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .responseBody("{\"result\":true}")
                .build();

            // when
            log.updateResponseBody("");

            // then
            assertThat(log.getResponseBody()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[updateHttpStatus] httpStatus를 업데이트하는 메서드")
    class Describe_updateHttpStatus {

        @Test
        @DisplayName("[success] httpStatus를 업데이트한다")
        void success() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .httpStatus(null)
                .build();

            // when
            log.updateHttpStatus("200");

            // then
            assertThat(log.getHttpStatus()).isEqualTo("200");
        }

        @Test
        @DisplayName("[success] httpStatus를 다양한 값으로 업데이트한다")
        void success_variousStatuses() {
            // given
            ApiCallLog log = ApiCallLog.builder().build();

            // when & then
            log.updateHttpStatus("200");
            assertThat(log.getHttpStatus()).isEqualTo("200");

            log.updateHttpStatus("400");
            assertThat(log.getHttpStatus()).isEqualTo("400");

            log.updateHttpStatus("500");
            assertThat(log.getHttpStatus()).isEqualTo("500");
        }

        @Test
        @DisplayName("[success] httpStatus를 null로 업데이트한다")
        void success_null() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .httpStatus("200")
                .build();

            // when
            log.updateHttpStatus(null);

            // then
            assertThat(log.getHttpStatus()).isNull();
        }
    }

    @Nested
    @DisplayName("[updateErrorCode] errorCode를 업데이트하는 메서드")
    class Describe_updateErrorCode {

        @Test
        @DisplayName("[success] errorCode를 업데이트한다")
        void success() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .errorCode(null)
                .build();

            // when
            log.updateErrorCode("INVALID_REQUEST");

            // then
            assertThat(log.getErrorCode()).isEqualTo("INVALID_REQUEST");
        }

        @Test
        @DisplayName("[success] errorCode를 다양한 값으로 업데이트한다")
        void success_variousErrorCodes() {
            // given
            ApiCallLog log = ApiCallLog.builder().build();

            // when & then
            log.updateErrorCode("BAD_REQUEST");
            assertThat(log.getErrorCode()).isEqualTo("BAD_REQUEST");

            log.updateErrorCode("NOT_FOUND");
            assertThat(log.getErrorCode()).isEqualTo("NOT_FOUND");

            log.updateErrorCode("INTERNAL_SERVER_ERROR");
            assertThat(log.getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
        }

        @Test
        @DisplayName("[success] errorCode를 null로 업데이트한다")
        void success_null() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .errorCode("INVALID_REQUEST")
                .build();

            // when
            log.updateErrorCode(null);

            // then
            assertThat(log.getErrorCode()).isNull();
        }
    }

    @Nested
    @DisplayName("[updateApiId] apiId를 업데이트하는 메서드")
    class Describe_updateApiId {

        @Test
        @DisplayName("[success] apiId를 업데이트한다")
        void success() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .apiId(null)
                .build();

            // when
            log.updateApiId(100L);

            // then
            assertThat(log.getApiId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("[success] apiId를 null로 업데이트한다")
        void success_null() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .apiId(100L)
                .build();

            // when
            log.updateApiId(null);

            // then
            assertThat(log.getApiId()).isNull();
        }

        @Test
        @DisplayName("[success] apiId를 다른 값으로 업데이트한다")
        void success_changeValue() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .apiId(100L)
                .build();

            // when
            log.updateApiId(200L);

            // then
            assertThat(log.getApiId()).isEqualTo(200L);
        }
    }

    @Nested
    @DisplayName("[updateRequestPathParam] requestPathParam을 업데이트하는 메서드")
    class Describe_updateRequestPathParam {

        @Test
        @DisplayName("[success] ApiInfo로부터 requestPathParam을 업데이트한다")
        void success() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .uri("/api/users/123")
                .build();
            ApiInfo apiInfo = new ApiInfo(
                1L,
                "example.com",
                "GET",
                "/api/users/{id}"
            );

            // when
            log.updateRequestPathParam(apiInfo);

            // then
            assertThat(log.getRequestPathParam()).isNotNull();
            assertThat(log.getRequestPathParam()).contains("id");
            assertThat(log.getRequestPathParam()).contains("123");
        }

        @Test
        @DisplayName("[success] 매칭되지 않는 URI는 빈 requestPathParam을 설정한다")
        void success_unmatchedUri() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .uri("/api/posts/123")
                .build();
            ApiInfo apiInfo = new ApiInfo(
                1L,
                "example.com",
                "GET",
                "/api/users/{id}"
            );

            // when
            log.updateRequestPathParam(apiInfo);

            // then
            assertThat(log.getRequestPathParam()).isEmpty();
        }

        @Test
        @DisplayName("[success] 예외 발생 시 빈 문자열을 설정한다")
        void success_exceptionHandling() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .uri(null)
                .build();
            ApiInfo apiInfo = new ApiInfo(
                1L,
                "example.com",
                "GET",
                "/api/users/{id}"
            );

            // when
            log.updateRequestPathParam(apiInfo);

            // then
            assertThat(log.getRequestPathParam()).isEmpty();
        }

        @Test
        @DisplayName("[success] 여러 경로 변수를 포함한 requestPathParam을 업데이트한다")
        void success_multiplePathVariables() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .uri("/api/users/123/posts/456")
                .build();
            ApiInfo apiInfo = new ApiInfo(
                1L,
                "example.com",
                "GET",
                "/api/users/{userId}/posts/{postId}"
            );

            // when
            log.updateRequestPathParam(apiInfo);

            // then
            assertThat(log.getRequestPathParam()).isNotNull();
            assertThat(log.getRequestPathParam()).contains("userId");
            assertThat(log.getRequestPathParam()).contains("123");
            assertThat(log.getRequestPathParam()).contains("postId");
            assertThat(log.getRequestPathParam()).contains("456");
        }
    }

    @Nested
    @DisplayName("[getRequestLog] 요청 로그 문자열을 반환하는 메서드")
    class Describe_getRequestLog {

        @Test
        @DisplayName("[success] 요청 로그 문자열을 반환한다")
        void success() {
            // given
            ObjectNode accountInfo = objectMapper.createObjectNode()
                .put("email", "test@example.com")
                .put("id", 1L);
            ApiCallLog log = ApiCallLog.builder()
                .httpMethod("GET")
                .uri("/api/users/123")
                .requestParam("{page=1}")
                .requestBody("{\"name\":\"test\"}")
                .accountInfo(accountInfo)
                .build();

            // when
            String result = log.getRequestLog();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("GET");
            assertThat(result).contains("/api/users/123");
            assertThat(result).contains("request");
            assertThat(result).contains("{page=1}");
            assertThat(result).contains("{\"name\":\"test\"}");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 요청 로그 문자열을 반환한다")
        void success_nullValues() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .httpMethod(null)
                .uri(null)
                .requestParam(null)
                .requestBody(null)
                .accountInfo(null)
                .build();

            // when
            String result = log.getRequestLog();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("null");
        }

        @Test
        @DisplayName("[success] POST 요청 로그 문자열을 반환한다")
        void success_postRequest() {
            // given
            ObjectNode accountInfo = objectMapper.createObjectNode()
                .put("email", "test@example.com");
            ApiCallLog log = ApiCallLog.builder()
                .httpMethod("POST")
                .uri("/api/users")
                .requestParam(null)
                .requestBody("{\"name\":\"홍길동\",\"email\":\"hong@example.com\"}")
                .accountInfo(accountInfo)
                .build();

            // when
            String result = log.getRequestLog();

            // then
            assertThat(result).contains("POST");
            assertThat(result).contains("/api/users");
            assertThat(result).contains("{\"name\":\"홍길동\",\"email\":\"hong@example.com\"}");
        }
    }

    @Nested
    @DisplayName("[getResponseLog] 응답 로그 문자열을 반환하는 메서드")
    class Describe_getResponseLog {

        @Test
        @DisplayName("[success] 응답 로그 문자열을 반환한다")
        void success() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .httpMethod("GET")
                .uri("/api/users/123")
                .responseBody("{\"result\":true}")
                .build();

            // when
            String result = log.getResponseLog();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("GET");
            assertThat(result).contains("/api/users/123");
            assertThat(result).contains("response");
            assertThat(result).contains("{\"result\":true}");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 응답 로그 문자열을 반환한다")
        void success_nullValues() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .httpMethod(null)
                .uri(null)
                .responseBody(null)
                .build();

            // when
            String result = log.getResponseLog();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("null");
        }

        @Test
        @DisplayName("[success] 에러 응답 로그 문자열을 반환한다")
        void success_errorResponse() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .httpMethod("POST")
                .uri("/api/users")
                .responseBody("{\"error\":\"INVALID_REQUEST\",\"message\":\"잘못된 요청입니다\"}")
                .build();

            // when
            String result = log.getResponseLog();

            // then
            assertThat(result).contains("POST");
            assertThat(result).contains("/api/users");
            assertThat(result).contains(
                "{\"error\":\"INVALID_REQUEST\",\"message\":\"잘못된 요청입니다\"}");
        }
    }

    @Nested
    @DisplayName("[getter] Getter 메서드 테스트")
    class Describe_getter {

        @Test
        @DisplayName("[success] getAccountInfo()로 accountInfo를 조회한다")
        void success_getAccountInfo() {
            // given
            ObjectNode accountInfo = objectMapper.createObjectNode()
                .put("email", "test@example.com");
            ApiCallLog log = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .build();

            // when
            ObjectNode result = log.getAccountInfo();

            // then
            assertThat(result).isEqualTo(accountInfo);
        }

        @Test
        @DisplayName("[success] getApiId()로 apiId를 조회한다")
        void success_getApiId() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .apiId(100L)
                .build();

            // when
            Long apiId = log.getApiId();

            // then
            assertThat(apiId).isEqualTo(100L);
        }

        @Test
        @DisplayName("[success] getUri()로 uri를 조회한다")
        void success_getUri() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .uri("/api/users/123")
                .build();

            // when
            String uri = log.getUri();

            // then
            assertThat(uri).isEqualTo("/api/users/123");
        }

        @Test
        @DisplayName("[success] getHttpMethod()로 httpMethod를 조회한다")
        void success_getHttpMethod() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .httpMethod("GET")
                .build();

            // when
            String httpMethod = log.getHttpMethod();

            // then
            assertThat(httpMethod).isEqualTo("GET");
        }

        @Test
        @DisplayName("[success] getRequestPathParam()으로 requestPathParam을 조회한다")
        void success_getRequestPathParam() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .requestPathParam("{id=123}")
                .build();

            // when
            String requestPathParam = log.getRequestPathParam();

            // then
            assertThat(requestPathParam).isEqualTo("{id=123}");
        }

        @Test
        @DisplayName("[success] getRequestParam()으로 requestParam을 조회한다")
        void success_getRequestParam() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .requestParam("{page=1}")
                .build();

            // when
            String requestParam = log.getRequestParam();

            // then
            assertThat(requestParam).isEqualTo("{page=1}");
        }

        @Test
        @DisplayName("[success] getRequestBody()로 requestBody를 조회한다")
        void success_getRequestBody() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .requestBody("{\"name\":\"test\"}")
                .build();

            // when
            String requestBody = log.getRequestBody();

            // then
            assertThat(requestBody).isEqualTo("{\"name\":\"test\"}");
        }

        @Test
        @DisplayName("[success] getResponseBody()로 responseBody를 조회한다")
        void success_getResponseBody() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .responseBody("{\"result\":true}")
                .build();

            // when
            String responseBody = log.getResponseBody();

            // then
            assertThat(responseBody).isEqualTo("{\"result\":true}");
        }

        @Test
        @DisplayName("[success] getHttpStatus()로 httpStatus를 조회한다")
        void success_getHttpStatus() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .httpStatus("200")
                .build();

            // when
            String httpStatus = log.getHttpStatus();

            // then
            assertThat(httpStatus).isEqualTo("200");
        }

        @Test
        @DisplayName("[success] getErrorCode()로 errorCode를 조회한다")
        void success_getErrorCode() {
            // given
            ApiCallLog log = ApiCallLog.builder()
                .errorCode("INVALID_REQUEST")
                .build();

            // when
            String errorCode = log.getErrorCode();

            // then
            assertThat(errorCode).isEqualTo("INVALID_REQUEST");
        }

        @Test
        @DisplayName("[success] getRegDt()로 regDt를 조회한다")
        void success_getRegDt() {
            // given
            LocalDateTime now = LocalDateTime.now();
            ApiCallLog log = ApiCallLog.builder()
                .regDt(now)
                .build();

            // when
            LocalDateTime regDt = log.getRegDt();

            // then
            assertThat(regDt).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("[noArgsConstructor] NoArgsConstructor 테스트")
    class Describe_noArgsConstructor {

        @Test
        @DisplayName("[success] NoArgsConstructor로 ApiCallLog를 생성한다")
        void success() {
            // when
            ApiCallLog log = new ApiCallLog();

            // then
            assertThat(log).isNotNull();
        }
    }

    @Nested
    @DisplayName("[allArgsConstructor] AllArgsConstructor 테스트")
    class Describe_allArgsConstructor {

        @Test
        @DisplayName("[success] AllArgsConstructor로 ApiCallLog를 생성한다")
        void success() {
            // given
            ObjectNode accountInfo = objectMapper.createObjectNode()
                .put("email", "test@example.com");
            LocalDateTime now = LocalDateTime.now();

            // when
            ApiCallLog log = new ApiCallLog(
                accountInfo,
                100L,
                "/api/users/123",
                "GET",
                "{id=123}",
                "{page=1}",
                "{\"name\":\"test\"}",
                "{\"result\":true}",
                "200",
                null,
                now
            );

            // then
            assertThat(log).isNotNull();
            assertThat(log.getAccountInfo()).isEqualTo(accountInfo);
            assertThat(log.getApiId()).isEqualTo(100L);
            assertThat(log.getUri()).isEqualTo("/api/users/123");
            assertThat(log.getHttpMethod()).isEqualTo("GET");
            assertThat(log.getRequestPathParam()).isEqualTo("{id=123}");
            assertThat(log.getRequestParam()).isEqualTo("{page=1}");
            assertThat(log.getRequestBody()).isEqualTo("{\"name\":\"test\"}");
            assertThat(log.getResponseBody()).isEqualTo("{\"result\":true}");
            assertThat(log.getHttpStatus()).isEqualTo("200");
            assertThat(log.getErrorCode()).isNull();
            assertThat(log.getRegDt()).isEqualTo(now);
        }
    }
}
