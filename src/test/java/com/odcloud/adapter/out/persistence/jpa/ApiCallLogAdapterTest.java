package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.ApiCallLog;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ApiCallLogAdapterTest extends IntegrationTestSupport {

    @Autowired
    ApiCallLogAdapter adapter;

    @Autowired
    EntityManager entityManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM ApiCallLogEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM ApiInfoEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("[register] API 호출 로그를 등록하는 메소드")
    class Describe_register {

        @Test
        @DisplayName("[success] API 호출 로그를 등록한다")
        void success() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");
            accountInfo.put("name", "홍길동");

            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(1L)
                .uri("/api/accounts")
                .httpMethod("GET")
                .requestPathParam("{}")
                .requestParam("{\"page\": 1}")
                .requestBody("{}")
                .httpStatus("200")
                .errorCode(null)
                .regDt(now)
                .build();

            // when
            ApiCallLog result = adapter.register(apiCallLog);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();

            var savedLogs = entityManager
                .createQuery("SELECT a FROM ApiCallLogEntity a WHERE a.apiId = :apiId",
                    ApiCallLogEntity.class)
                .setParameter("apiId", 1L)
                .getResultList();

            assertThat(savedLogs).hasSize(1);
            assertThat(savedLogs.get(0).getEmail()).isEqualTo("test@example.com");
            assertThat(savedLogs.get(0).getApiId()).isEqualTo(1L);
            assertThat(savedLogs.get(0).getRequestPathParam()).isEqualTo("{}");
            assertThat(savedLogs.get(0).getRequestParam()).isEqualTo("{\"page\": 1}");
            assertThat(savedLogs.get(0).getRequestBody()).isEqualTo("{}");
            assertThat(savedLogs.get(0).getHttpStatus()).isEqualTo("200");
            assertThat(savedLogs.get(0).getErrorCode()).isNull();
        }

        @Test
        @DisplayName("[success] 에러가 발생한 API 호출 로그를 등록한다")
        void success_withError() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(1L)
                .uri("/api/accounts/999")
                .httpMethod("GET")
                .requestPathParam("{\"id\": 999}")
                .requestParam("{}")
                .requestBody("{}")
                .httpStatus("404")
                .errorCode("ACCOUNT_NOT_FOUND")
                .regDt(now)
                .build();

            // when
            ApiCallLog result = adapter.register(apiCallLog);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(result).isNotNull();

            var savedLogs = entityManager
                .createQuery("SELECT a FROM ApiCallLogEntity a WHERE a.apiId = :apiId",
                    ApiCallLogEntity.class)
                .setParameter("apiId", 1L)
                .getResultList();

            assertThat(savedLogs).hasSize(1);
            assertThat(savedLogs.get(0).getHttpStatus()).isEqualTo("404");
            assertThat(savedLogs.get(0).getErrorCode()).isEqualTo("ACCOUNT_NOT_FOUND");
        }

        @Test
        @DisplayName("[success] 여러 API 호출 로그를 등록한다")
        void success_multipleLogs() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            ApiCallLog log1 = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(1L)
                .uri("/api/accounts")
                .httpMethod("GET")
                .requestPathParam("{}")
                .requestParam("{}")
                .requestBody("{}")
                .httpStatus("200")
                .regDt(now)
                .build();

            ApiCallLog log2 = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(2L)
                .uri("/api/groups")
                .httpMethod("POST")
                .requestPathParam("{}")
                .requestParam("{}")
                .requestBody("{\"name\": \"test\"}")
                .httpStatus("201")
                .regDt(now.plusSeconds(1))
                .build();

            ApiCallLog log3 = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(3L)
                .uri("/api/files")
                .httpMethod("DELETE")
                .requestPathParam("{\"id\": 1}")
                .requestParam("{}")
                .requestBody("{}")
                .httpStatus("204")
                .regDt(now.plusSeconds(2))
                .build();

            // when
            adapter.register(log1);
            adapter.register(log2);
            adapter.register(log3);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedLogs = entityManager
                .createQuery("SELECT a FROM ApiCallLogEntity a ORDER BY a.regDt",
                    ApiCallLogEntity.class)
                .getResultList();

            assertThat(savedLogs).hasSize(3);
            assertThat(savedLogs.get(0).getHttpStatus()).isEqualTo("200");
            assertThat(savedLogs.get(1).getHttpStatus()).isEqualTo("201");
            assertThat(savedLogs.get(2).getHttpStatus()).isEqualTo("204");
        }

        @Test
        @DisplayName("[success] POST 요청의 request body를 포함한 로그를 등록한다")
        void success_postWithRequestBody() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            String requestBody = "{\"name\": \"홍길동\", \"email\": \"hong@example.com\"}";

            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(1L)
                .uri("/api/accounts")
                .httpMethod("POST")
                .requestPathParam("{}")
                .requestParam("{}")
                .requestBody(requestBody)
                .httpStatus("201")
                .regDt(now)
                .build();

            // when
            adapter.register(apiCallLog);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedLogs = entityManager
                .createQuery("SELECT a FROM ApiCallLogEntity a WHERE a.apiId = :apiId",
                    ApiCallLogEntity.class)
                .setParameter("apiId", 1L)
                .getResultList();

            assertThat(savedLogs).hasSize(1);
            assertThat(savedLogs.get(0).getRequestBody()).isEqualTo(requestBody);
        }

        @Test
        @DisplayName("[success] query parameter를 포함한 로그를 등록한다")
        void success_withQueryParameter() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            String requestParam = "{\"page\": 1, \"size\": 10, \"sort\": \"name,asc\"}";

            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(1L)
                .uri("/api/accounts")
                .httpMethod("GET")
                .requestPathParam("{}")
                .requestParam(requestParam)
                .requestBody("{}")
                .httpStatus("200")
                .regDt(now)
                .build();

            // when
            adapter.register(apiCallLog);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedLogs = entityManager
                .createQuery("SELECT a FROM ApiCallLogEntity a WHERE a.apiId = :apiId",
                    ApiCallLogEntity.class)
                .setParameter("apiId", 1L)
                .getResultList();

            assertThat(savedLogs).hasSize(1);
            assertThat(savedLogs.get(0).getRequestParam()).isEqualTo(requestParam);
        }

        @Test
        @DisplayName("[success] path variable을 포함한 로그를 등록한다")
        void success_withPathVariable() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            String requestPathParam = "{\"id\": 123}";

            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(1L)
                .uri("/api/accounts/123")
                .httpMethod("GET")
                .requestPathParam(requestPathParam)
                .requestParam("{}")
                .requestBody("{}")
                .httpStatus("200")
                .regDt(now)
                .build();

            // when
            adapter.register(apiCallLog);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedLogs = entityManager
                .createQuery("SELECT a FROM ApiCallLogEntity a WHERE a.apiId = :apiId",
                    ApiCallLogEntity.class)
                .setParameter("apiId", 1L)
                .getResultList();

            assertThat(savedLogs).hasSize(1);
            assertThat(savedLogs.get(0).getRequestPathParam()).isEqualTo(requestPathParam);
        }

        @Test
        @DisplayName("[success] 다양한 HTTP 상태 코드를 기록한다")
        void success_variousHttpStatus() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            ApiCallLog log200 = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(1L)
                .uri("/api/data")
                .httpMethod("GET")
                .requestPathParam("{}")
                .requestParam("{}")
                .requestBody("{}")
                .httpStatus("200")
                .regDt(now)
                .build();

            ApiCallLog log400 = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(2L)
                .uri("/api/data")
                .httpMethod("POST")
                .requestPathParam("{}")
                .requestParam("{}")
                .requestBody("{}")
                .httpStatus("400")
                .errorCode("BAD_REQUEST")
                .regDt(now)
                .build();

            ApiCallLog log500 = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(3L)
                .uri("/api/data")
                .httpMethod("GET")
                .requestPathParam("{}")
                .requestParam("{}")
                .requestBody("{}")
                .httpStatus("500")
                .errorCode("INTERNAL_SERVER_ERROR")
                .regDt(now)
                .build();

            // when
            adapter.register(log200);
            adapter.register(log400);
            adapter.register(log500);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedLogs = entityManager
                .createQuery("SELECT a FROM ApiCallLogEntity a ORDER BY a.apiId",
                    ApiCallLogEntity.class)
                .getResultList();

            assertThat(savedLogs).hasSize(3);
            assertThat(savedLogs.get(0).getHttpStatus()).isEqualTo("200");
            assertThat(savedLogs.get(1).getHttpStatus()).isEqualTo("400");
            assertThat(savedLogs.get(2).getHttpStatus()).isEqualTo("500");
        }

        @Test
        @DisplayName("[success] accountInfo에 email이 없어도 등록한다")
        void success_noEmailInAccountInfo() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("name", "익명");

            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(1L)
                .uri("/api/public/data")
                .httpMethod("GET")
                .requestPathParam("{}")
                .requestParam("{}")
                .requestBody("{}")
                .httpStatus("200")
                .regDt(now)
                .build();

            // when
            adapter.register(apiCallLog);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedLogs = entityManager
                .createQuery("SELECT a FROM ApiCallLogEntity a WHERE a.apiId = :apiId",
                    ApiCallLogEntity.class)
                .setParameter("apiId", 1L)
                .getResultList();

            assertThat(savedLogs).hasSize(1);
            assertThat(savedLogs.get(0).getEmail()).isEqualTo("");
        }

        @Test
        @DisplayName("[success] 여러 사용자의 API 호출 로그를 등록한다")
        void success_multipleUsers() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ObjectNode user1Info = objectMapper.createObjectNode();
            user1Info.put("email", "user1@example.com");

            ObjectNode user2Info = objectMapper.createObjectNode();
            user2Info.put("email", "user2@example.com");

            ApiCallLog log1 = ApiCallLog.builder()
                .accountInfo(user1Info)
                .apiId(1L)
                .uri("/api/accounts")
                .httpMethod("GET")
                .requestPathParam("{}")
                .requestParam("{}")
                .requestBody("{}")
                .httpStatus("200")
                .regDt(now)
                .build();

            ApiCallLog log2 = ApiCallLog.builder()
                .accountInfo(user2Info)
                .apiId(1L)
                .uri("/api/accounts")
                .httpMethod("GET")
                .requestPathParam("{}")
                .requestParam("{}")
                .requestBody("{}")
                .httpStatus("200")
                .regDt(now.plusSeconds(1))
                .build();

            // when
            adapter.register(log1);
            adapter.register(log2);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedLogs = entityManager
                .createQuery("SELECT a FROM ApiCallLogEntity a ORDER BY a.regDt",
                    ApiCallLogEntity.class)
                .getResultList();

            assertThat(savedLogs).hasSize(2);
            assertThat(savedLogs.get(0).getEmail()).isEqualTo("user1@example.com");
            assertThat(savedLogs.get(1).getEmail()).isEqualTo("user2@example.com");
        }

        @Test
        @DisplayName("[success] null errorCode로 로그를 등록한다")
        void success_nullErrorCode() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(1L)
                .uri("/api/accounts")
                .httpMethod("GET")
                .requestPathParam("{}")
                .requestParam("{}")
                .requestBody("{}")
                .httpStatus("200")
                .errorCode(null)
                .regDt(now)
                .build();

            // when
            adapter.register(apiCallLog);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedLogs = entityManager
                .createQuery("SELECT a FROM ApiCallLogEntity a WHERE a.apiId = :apiId",
                    ApiCallLogEntity.class)
                .setParameter("apiId", 1L)
                .getResultList();

            assertThat(savedLogs).hasSize(1);
            assertThat(savedLogs.get(0).getErrorCode()).isNull();
        }

        @Test
        @DisplayName("[success] 같은 API를 여러 번 호출한 로그를 등록한다")
        void success_sameApiMultipleTimes() {
            // given
            LocalDateTime now = LocalDateTime.now();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            ApiCallLog log1 = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(1L)
                .uri("/api/accounts")
                .httpMethod("GET")
                .requestPathParam("{}")
                .requestParam("{\"page\": 1}")
                .requestBody("{}")
                .httpStatus("200")
                .regDt(now)
                .build();

            ApiCallLog log2 = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(1L)
                .uri("/api/accounts")
                .httpMethod("GET")
                .requestPathParam("{}")
                .requestParam("{\"page\": 2}")
                .requestBody("{}")
                .httpStatus("200")
                .regDt(now.plusSeconds(1))
                .build();

            ApiCallLog log3 = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .apiId(1L)
                .uri("/api/accounts")
                .httpMethod("GET")
                .requestPathParam("{}")
                .requestParam("{\"page\": 3}")
                .requestBody("{}")
                .httpStatus("200")
                .regDt(now.plusSeconds(2))
                .build();

            // when
            adapter.register(log1);
            adapter.register(log2);
            adapter.register(log3);
            entityManager.flush();
            entityManager.clear();

            // then
            var savedLogs = entityManager
                .createQuery(
                    "SELECT a FROM ApiCallLogEntity a WHERE a.apiId = :apiId ORDER BY a.regDt",
                    ApiCallLogEntity.class)
                .setParameter("apiId", 1L)
                .getResultList();

            assertThat(savedLogs).hasSize(3);
            assertThat(savedLogs.get(0).getRequestParam()).isEqualTo("{\"page\": 1}");
            assertThat(savedLogs.get(1).getRequestParam()).isEqualTo("{\"page\": 2}");
            assertThat(savedLogs.get(2).getRequestParam()).isEqualTo("{\"page\": 3}");
        }
    }
}
