package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.ApiCallLog;
import com.odcloud.domain.model.ApiInfo;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ApiInfoStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    ApiInfoStorageAdapter adapter;

    @Autowired
    EntityManager entityManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM ApiInfoEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("[findAll] 모든 API 정보를 조회하는 메소드")
    class Describe_findAll {

        @Test
        @DisplayName("[success] 모든 API 정보를 조회한다")
        void success() {
            // given
            entityManager.persist(ApiInfoEntity.builder()
                .domain("account")
                .httpMethod("GET")
                .uriPattern("/api/accounts")
                .build());

            entityManager.persist(ApiInfoEntity.builder()
                .domain("group")
                .httpMethod("POST")
                .uriPattern("/api/groups")
                .build());

            entityManager.persist(ApiInfoEntity.builder()
                .domain("file")
                .httpMethod("GET")
                .uriPattern("/api/files")
                .build());

            entityManager.flush();
            entityManager.clear();

            // when
            List<ApiInfo> result = adapter.findAll();

            // then
            assertThat(result).hasSize(3);
            assertThat(result)
                .extracting(ApiInfo::domain)
                .containsExactlyInAnyOrder("account", "group", "file");
        }

        @Test
        @DisplayName("[success] API 정보가 없으면 빈 리스트를 반환한다")
        void success_empty() {
            // when
            List<ApiInfo> result = adapter.findAll();

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] URI 패턴의 path variable 개수로 정렬한다")
        void success_sortedByPathVariableCount() {
            // given
            entityManager.persist(ApiInfoEntity.builder()
                .domain("api1")
                .httpMethod("GET")
                .uriPattern("/api/users/{id}/posts/{postId}")  // 2개
                .build());

            entityManager.persist(ApiInfoEntity.builder()
                .domain("api2")
                .httpMethod("GET")
                .uriPattern("/api/users")  // 0개
                .build());

            entityManager.persist(ApiInfoEntity.builder()
                .domain("api3")
                .httpMethod("GET")
                .uriPattern("/api/users/{id}")  // 1개
                .build());

            entityManager.flush();
            entityManager.clear();

            // when
            List<ApiInfo> result = adapter.findAll();

            // then
            assertThat(result).hasSize(3);
            // path variable이 적은 순서로 정렬되어야 함
            assertThat(result.get(0).uriPattern()).isEqualTo("/api/users");
            assertThat(result.get(1).uriPattern()).isEqualTo("/api/users/{id}");
            assertThat(result.get(2).uriPattern()).isEqualTo("/api/users/{id}/posts/{postId}");
        }
        
        @Test
        @DisplayName("[success] 다양한 HTTP 메서드의 API를 조회한다")
        void success_variousHttpMethods() {
            // given
            entityManager.persist(ApiInfoEntity.builder()
                .domain("api")
                .httpMethod("GET")
                .uriPattern("/api/data")
                .build());

            entityManager.persist(ApiInfoEntity.builder()
                .domain("api")
                .httpMethod("POST")
                .uriPattern("/api/data")
                .build());

            entityManager.persist(ApiInfoEntity.builder()
                .domain("api")
                .httpMethod("PUT")
                .uriPattern("/api/data/{id}")
                .build());

            entityManager.persist(ApiInfoEntity.builder()
                .domain("api")
                .httpMethod("DELETE")
                .uriPattern("/api/data/{id}")
                .build());

            entityManager.flush();
            entityManager.clear();

            // when
            List<ApiInfo> result = adapter.findAll();

            // then
            assertThat(result).hasSize(4);
            assertThat(result)
                .extracting(ApiInfo::httpMethod)
                .containsExactlyInAnyOrder("GET", "POST", "PUT", "DELETE");
        }
    }

    @Nested
    @DisplayName("[findByApiCallLog] API 호출 로그로 매칭되는 API 정보를 조회하는 메소드")
    class Describe_findByApiCallLog {

        @Test
        @DisplayName("[success] 정확히 매칭되는 API 정보를 조회한다")
        void success_exactMatch() {
            // given
            entityManager.persist(ApiInfoEntity.builder()
                .domain("account")
                .httpMethod("GET")
                .uriPattern("/api/accounts")
                .build());

            entityManager.flush();
            entityManager.clear();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .uri("/api/accounts")
                .httpMethod("GET")
                .requestParam("{}")
                .requestBody("{}")
                .regDt(LocalDateTime.now())
                .build();

            // when
            ApiInfo result = adapter.findByApiCallLog(apiCallLog);

            // then
            assertThat(result).isNotNull();
            assertThat(result.domain()).isEqualTo("account");
            assertThat(result.httpMethod()).isEqualTo("GET");
            assertThat(result.uriPattern()).isEqualTo("/api/accounts");
        }

        @Test
        @DisplayName("[success] path variable이 있는 URI를 매칭한다")
        void success_withPathVariable() {
            // given
            entityManager.persist(ApiInfoEntity.builder()
                .domain("account")
                .httpMethod("GET")
                .uriPattern("/api/accounts/{id}")
                .build());

            entityManager.flush();
            entityManager.clear();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .uri("/api/accounts/123")
                .httpMethod("GET")
                .requestParam("{}")
                .requestBody("{}")
                .regDt(LocalDateTime.now())
                .build();

            // when
            ApiInfo result = adapter.findByApiCallLog(apiCallLog);

            // then
            assertThat(result).isNotNull();
            assertThat(result.domain()).isEqualTo("account");
            assertThat(result.uriPattern()).isEqualTo("/api/accounts/{id}");
        }

        @Test
        @DisplayName("[success] 여러 path variable이 있는 URI를 매칭한다")
        void success_multiplePathVariables() {
            // given
            entityManager.persist(ApiInfoEntity.builder()
                .domain("post")
                .httpMethod("GET")
                .uriPattern("/api/users/{userId}/posts/{postId}")
                .build());

            entityManager.flush();
            entityManager.clear();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .uri("/api/users/100/posts/200")
                .httpMethod("GET")
                .requestParam("{}")
                .requestBody("{}")
                .regDt(LocalDateTime.now())
                .build();

            // when
            ApiInfo result = adapter.findByApiCallLog(apiCallLog);

            // then
            assertThat(result).isNotNull();
            assertThat(result.domain()).isEqualTo("post");
            assertThat(result.uriPattern()).isEqualTo("/api/users/{userId}/posts/{postId}");
        }

        @Test
        @DisplayName("[success] HTTP 메서드도 함께 확인한다")
        void success_withHttpMethod() {
            // given
            entityManager.persist(ApiInfoEntity.builder()
                .domain("api")
                .httpMethod("GET")
                .uriPattern("/api/data")
                .build());

            entityManager.persist(ApiInfoEntity.builder()
                .domain("api")
                .httpMethod("POST")
                .uriPattern("/api/data")
                .build());

            entityManager.flush();
            entityManager.clear();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            ApiCallLog getCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .uri("/api/data")
                .httpMethod("GET")
                .requestParam("{}")
                .requestBody("{}")
                .regDt(LocalDateTime.now())
                .build();

            ApiCallLog postCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .uri("/api/data")
                .httpMethod("POST")
                .requestParam("{}")
                .requestBody("{}")
                .regDt(LocalDateTime.now())
                .build();

            // when
            ApiInfo getResult = adapter.findByApiCallLog(getCallLog);
            ApiInfo postResult = adapter.findByApiCallLog(postCallLog);

            // then
            assertThat(getResult).isNotNull();
            assertThat(getResult.httpMethod()).isEqualTo("GET");
            assertThat(postResult).isNotNull();
            assertThat(postResult.httpMethod()).isEqualTo("POST");
        }

        @Test
        @DisplayName("[success] 매칭되는 API가 없으면 null을 반환한다")
        void success_noMatch() {
            // given
            entityManager.persist(ApiInfoEntity.builder()
                .domain("account")
                .httpMethod("GET")
                .uriPattern("/api/accounts")
                .build());

            entityManager.flush();
            entityManager.clear();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .uri("/api/groups")  // 다른 URI
                .httpMethod("GET")
                .requestParam("{}")
                .requestBody("{}")
                .regDt(LocalDateTime.now())
                .build();

            // when
            ApiInfo result = adapter.findByApiCallLog(apiCallLog);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("[success] 여러 후보 중 가장 먼저 매칭되는 API를 반환한다")
        void success_firstMatch() {
            // given
            // path variable이 적은 것이 먼저 매칭됨
            entityManager.persist(ApiInfoEntity.builder()
                .domain("api1")
                .httpMethod("GET")
                .uriPattern("/api/users/{id}/posts/{postId}")
                .build());

            entityManager.persist(ApiInfoEntity.builder()
                .domain("api2")
                .httpMethod("GET")
                .uriPattern("/api/users/{id}")
                .build());

            entityManager.flush();
            entityManager.clear();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            // /api/users/123은 두 패턴 모두에 매칭되지만, path variable이 적은 것이 먼저 반환됨
            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .uri("/api/users/123")
                .httpMethod("GET")
                .requestParam("{}")
                .requestBody("{}")
                .regDt(LocalDateTime.now())
                .build();

            // when
            ApiInfo result = adapter.findByApiCallLog(apiCallLog);

            // then
            assertThat(result).isNotNull();
            assertThat(result.domain()).isEqualTo("api2");
            assertThat(result.uriPattern()).isEqualTo("/api/users/{id}");
        }

        @Test
        @DisplayName("[success] HTTP 메서드가 다르면 매칭되지 않는다")
        void success_differentHttpMethod() {
            // given
            entityManager.persist(ApiInfoEntity.builder()
                .domain("account")
                .httpMethod("GET")
                .uriPattern("/api/accounts")
                .build());

            entityManager.flush();
            entityManager.clear();

            ObjectNode accountInfo = objectMapper.createObjectNode();
            accountInfo.put("email", "test@example.com");

            ApiCallLog apiCallLog = ApiCallLog.builder()
                .accountInfo(accountInfo)
                .uri("/api/accounts")
                .httpMethod("POST")  // 다른 HTTP 메서드
                .requestParam("{}")
                .requestBody("{}")
                .regDt(LocalDateTime.now())
                .build();

            // when
            ApiInfo result = adapter.findByApiCallLog(apiCallLog);

            // then
            assertThat(result).isNull();
        }
    }
}
