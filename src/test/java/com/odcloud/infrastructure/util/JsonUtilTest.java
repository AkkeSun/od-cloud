package com.odcloud.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.ContentCachingRequestWrapper;

@ExtendWith(MockitoExtension.class)
class JsonUtilTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private ContentCachingRequestWrapper wrappedRequest;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class TestObject {

        private String name;
        private int age;
        private String email;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class TestObjectWithDateTime {

        private String name;
        private LocalDateTime createdAt;
    }

    @Nested
    @DisplayName("[parseJson] JSON 문자열을 객체로 변환")
    class Describe_parseJson {

        @Test
        @DisplayName("[success] 정상적으로 JSON을 객체로 변환한다")
        void success() {
            // given
            String json = "{\"name\":\"홍길동\",\"age\":30,\"email\":\"test@example.com\"}";

            // when
            TestObject result = JsonUtil.parseJson(json, TestObject.class);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("홍길동");
            assertThat(result.getAge()).isEqualTo(30);
            assertThat(result.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("[failure] 유효하지 않은 JSON 형식이면 예외가 발생한다")
        void failure_invalidJson() {
            // given
            String invalidJson = "{invalid json}";

            // when & then
            assertThatThrownBy(() -> JsonUtil.parseJson(invalidJson, TestObject.class))
                .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("[success] LocalDateTime을 포함한 객체를 파싱한다")
        void success_withLocalDateTime() {
            // given
            String json = "{\"name\":\"test\",\"createdAt\":\"2024-01-01T10:00:00\"}";

            // when
            TestObjectWithDateTime result = JsonUtil.parseJson(json, TestObjectWithDateTime.class);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("test");
            assertThat(result.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("[toJsonString] 객체를 JSON 문자열로 변환")
    class Describe_toJsonString {

        @Test
        @DisplayName("[success] 정상적으로 객체를 JSON 문자열로 변환한다")
        void success() {
            // given
            TestObject obj = TestObject.builder()
                .name("홍길동")
                .age(30)
                .email("test@example.com")
                .build();

            // when
            String json = JsonUtil.toJsonString(obj);

            // then
            assertThat(json).isNotNull();
            assertThat(json).contains("\"name\":\"홍길동\"");
            assertThat(json).contains("\"age\":30");
            assertThat(json).contains("\"email\":\"test@example.com\"");
        }

        @Test
        @DisplayName("[success] null 필드를 포함한 객체를 JSON으로 변환한다")
        void success_withNullFields() {
            // given
            TestObject obj = TestObject.builder()
                .name("홍길동")
                .age(30)
                .build();

            // when
            String json = JsonUtil.toJsonString(obj);

            // then
            assertThat(json).isNotNull();
            assertThat(json).contains("\"name\":\"홍길동\"");
        }

        @Test
        @DisplayName("[success] LocalDateTime을 포함한 객체를 JSON으로 변환한다")
        void success_withLocalDateTime() {
            // given
            TestObjectWithDateTime obj = TestObjectWithDateTime.builder()
                .name("test")
                .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();

            // when
            String json = JsonUtil.toJsonString(obj);

            // then
            assertThat(json).isNotNull();
            assertThat(json).contains("\"name\":\"test\"");
        }
    }

    @Nested
    @DisplayName("[parseJsonList] JSON 배열을 리스트로 변환")
    class Describe_parseJsonList {

        @Test
        @DisplayName("[success] 정상적으로 JSON 배열을 리스트로 변환한다")
        void success() {
            // given
            String json = "[{\"name\":\"홍길동\",\"age\":30,\"email\":\"hong@example.com\"},"
                + "{\"name\":\"김철수\",\"age\":25,\"email\":\"kim@example.com\"}]";

            // when
            List<TestObject> result = JsonUtil.parseJsonList(json, TestObject.class);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("홍길동");
            assertThat(result.get(0).getAge()).isEqualTo(30);
            assertThat(result.get(1).getName()).isEqualTo("김철수");
            assertThat(result.get(1).getAge()).isEqualTo(25);
        }

        @Test
        @DisplayName("[success] 빈 JSON 배열을 빈 리스트로 변환한다")
        void success_emptyArray() {
            // given
            String json = "[]";

            // when
            List<TestObject> result = JsonUtil.parseJsonList(json, TestObject.class);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[failure] 유효하지 않은 JSON 배열이면 예외가 발생한다")
        void failure_invalidJsonArray() {
            // given
            String invalidJson = "[invalid json array]";

            // when & then
            assertThatThrownBy(() -> JsonUtil.parseJsonList(invalidJson, TestObject.class))
                .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("[toObjectNode] 객체를 ObjectNode로 변환")
    class Describe_toObjectNode {

        @Test
        @DisplayName("[success] 정상적으로 객체를 ObjectNode로 변환한다")
        void success() {
            // given
            TestObject obj = TestObject.builder()
                .name("홍길동")
                .age(30)
                .email("test@example.com")
                .build();

            // when
            ObjectNode result = JsonUtil.toObjectNode(obj);

            // then
            assertThat(result).isNotNull();
            assertThat(result.get("name").asText()).isEqualTo("홍길동");
            assertThat(result.get("age").asInt()).isEqualTo(30);
            assertThat(result.get("email").asText()).isEqualTo("test@example.com");
        }
    }

    @Nested
    @DisplayName("[extractJsonField] JSON에서 특정 필드 추출")
    class Describe_extractJsonField {

        @Test
        @DisplayName("[success] 정상적으로 JSON에서 필드를 추출한다")
        void success() {
            // given
            String json = "{\"name\":\"홍길동\",\"age\":30}";

            // when
            String name = JsonUtil.extractJsonField(json, "name");
            String age = JsonUtil.extractJsonField(json, "age");

            // then
            assertThat(name).isEqualTo("홍길동");
            assertThat(age).isEqualTo("30");
        }

        @Test
        @DisplayName("[success] 중첩된 JSON에서 필드를 추출한다")
        void success_nestedJson() {
            // given
            String json = "{\"data\":{\"user\":{\"name\":\"홍길동\"}}}";

            // when
            String name = JsonUtil.extractJsonField(json, "data", "user", "name");

            // then
            assertThat(name).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("[success] 존재하지 않는 필드는 빈 문자열을 반환한다")
        void success_nonExistentField() {
            // given
            String json = "{\"name\":\"홍길동\"}";

            // when
            String result = JsonUtil.extractJsonField(json, "nonexistent");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] 유효하지 않은 JSON은 빈 문자열을 반환한다")
        void success_invalidJson() {
            // given
            String invalidJson = "invalid json";

            // when
            String result = JsonUtil.extractJsonField(invalidJson, "field");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("[toJsonParams] HttpServletRequest의 파라미터를 JSON으로 변환")
    class Describe_toJsonParams {

        @Test
        @DisplayName("[success] 정상적으로 요청 파라미터를 JSON으로 변환한다")
        void success() {
            // given
            given(request.getParameterMap()).willReturn(
                java.util.Map.of(
                    "name", new String[]{"홍길동"},
                    "age", new String[]{"30"}
                )
            );

            // when
            String result = JsonUtil.toJsonParams(request);

            // then
            assertThat(result).contains("\"name\":\"홍길동\"");
            assertThat(result).contains("\"age\":\"30\"");
        }

        @Test
        @DisplayName("[success] 다중 값을 가진 파라미터를 JSON으로 변환한다")
        void success_multipleValues() {
            // given
            given(request.getParameterMap()).willReturn(
                java.util.Map.of(
                    "tags", new String[]{"tag1", "tag2", "tag3"}
                )
            );

            // when
            String result = JsonUtil.toJsonParams(request);

            // then
            assertThat(result).contains("\"tags\":\"tag1,tag2,tag3\"");
        }

        @Test
        @DisplayName("[success] 빈 파라미터 맵을 빈 JSON 객체로 변환한다")
        void success_emptyParams() {
            // given
            given(request.getParameterMap()).willReturn(java.util.Map.of());

            // when
            String result = JsonUtil.toJsonParams(request);

            // then
            assertThat(result).isEqualTo("{}");
        }
    }

    @Nested
    @DisplayName("[toJsonBody] ContentCachingRequestWrapper의 Body를 JSON으로 변환")
    class Describe_toJsonBody {

        @Test
        @DisplayName("[success] 정상적으로 요청 Body를 JSON으로 변환한다")
        void success() {
            // given
            String requestBody = "{\"name\":\"홍길동\",\"age\":30}";
            given(wrappedRequest.getContentAsByteArray()).willReturn(requestBody.getBytes());

            // when
            String result = JsonUtil.toJsonBody(wrappedRequest);

            // then
            assertThat(result).contains("\"name\":\"홍길동\"");
            assertThat(result).contains("\"age\":30");
        }

        @Test
        @DisplayName("[success] 빈 Body는 빈 JSON 객체를 반환한다")
        void success_emptyBody() {
            // given
            given(wrappedRequest.getContentAsByteArray()).willReturn("".getBytes());

            // when
            String result = JsonUtil.toJsonBody(wrappedRequest);

            // then
            assertThat(result).isEqualTo("{}");
        }

        @Test
        @DisplayName("[success] 유효하지 않은 JSON Body는 빈 JSON 객체를 반환한다")
        void success_invalidJsonBody() {
            // given
            given(wrappedRequest.getContentAsByteArray()).willReturn("invalid json".getBytes());

            // when
            String result = JsonUtil.toJsonBody(wrappedRequest);

            // then
            assertThat(result).isEqualTo("{}");
        }
    }

    @Nested
    @DisplayName("[maskPassword] JSON에서 password 필드 마스킹")
    class Describe_maskPassword {

        @Test
        @DisplayName("[success] password 필드를 SECRET으로 마스킹한다")
        void success() {
            // given
            String json = "{\"email\":\"test@example.com\",\"password\":\"myPassword123\"}";

            // when
            String result = JsonUtil.maskPassword(json);

            // then
            assertThat(result).contains("\"password\":\"SECRET\"");
            assertThat(result).contains("\"email\":\"test@example.com\"");
            assertThat(result).doesNotContain("myPassword123");
        }

        @Test
        @DisplayName("[success] password 필드가 없으면 원본 JSON을 반환한다")
        void success_noPasswordField() {
            // given
            String json = "{\"email\":\"test@example.com\",\"name\":\"홍길동\"}";

            // when
            String result = JsonUtil.maskPassword(json);

            // then
            assertThat(result).isEqualTo(json);
        }

        @Test
        @DisplayName("[success] 유효하지 않은 JSON은 원본을 반환한다")
        void success_invalidJson() {
            // given
            String invalidJson = "invalid json";

            // when
            String result = JsonUtil.maskPassword(invalidJson);

            // then
            assertThat(result).isEqualTo(invalidJson);
        }

        @Test
        @DisplayName("[success] JSON 배열은 원본을 반환한다")
        void success_jsonArray() {
            // given
            String jsonArray = "[{\"password\":\"pwd1\"},{\"password\":\"pwd2\"}]";

            // when
            String result = JsonUtil.maskPassword(jsonArray);

            // then
            assertThat(result).isEqualTo(jsonArray);
        }
    }
}
