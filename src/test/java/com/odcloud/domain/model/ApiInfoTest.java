package com.odcloud.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ApiInfoTest {

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 ApiInfo를 생성한다")
        void success() {
            // when
            ApiInfo apiInfo = ApiInfo.builder()
                .id(1L)
                .domain("example.com")
                .httpMethod("GET")
                .uriPattern("/api/users/{id}")
                .build();

            // then
            assertThat(apiInfo).isNotNull();
            assertThat(apiInfo.id()).isEqualTo(1L);
            assertThat(apiInfo.domain()).isEqualTo("example.com");
            assertThat(apiInfo.httpMethod()).isEqualTo("GET");
            assertThat(apiInfo.uriPattern()).isEqualTo("/api/users/{id}");
        }

        @Test
        @DisplayName("[success] null 값으로 ApiInfo를 생성한다")
        void success_nullValues() {
            // when
            ApiInfo apiInfo = ApiInfo.builder()
                .id(null)
                .domain(null)
                .httpMethod(null)
                .uriPattern(null)
                .build();

            // then
            assertThat(apiInfo.id()).isNull();
            assertThat(apiInfo.domain()).isNull();
            assertThat(apiInfo.httpMethod()).isNull();
            assertThat(apiInfo.uriPattern()).isNull();
        }

        @Test
        @DisplayName("[success] 다양한 HTTP 메서드로 ApiInfo를 생성한다")
        void success_variousHttpMethods() {
            // when
            ApiInfo getApi = ApiInfo.builder().httpMethod("GET").build();
            ApiInfo postApi = ApiInfo.builder().httpMethod("POST").build();
            ApiInfo putApi = ApiInfo.builder().httpMethod("PUT").build();
            ApiInfo deleteApi = ApiInfo.builder().httpMethod("DELETE").build();

            // then
            assertThat(getApi.httpMethod()).isEqualTo("GET");
            assertThat(postApi.httpMethod()).isEqualTo("POST");
            assertThat(putApi.httpMethod()).isEqualTo("PUT");
            assertThat(deleteApi.httpMethod()).isEqualTo("DELETE");
        }
    }

    @Nested
    @DisplayName("[constructor] Constructor 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] Constructor로 ApiInfo를 생성한다")
        void success() {
            // when
            ApiInfo apiInfo = new ApiInfo(
                1L,
                "example.com",
                "GET",
                "/api/users/{id}"
            );

            // then
            assertThat(apiInfo).isNotNull();
            assertThat(apiInfo.id()).isEqualTo(1L);
            assertThat(apiInfo.domain()).isEqualTo("example.com");
            assertThat(apiInfo.httpMethod()).isEqualTo("GET");
            assertThat(apiInfo.uriPattern()).isEqualTo("/api/users/{id}");
        }

        @Test
        @DisplayName("[success] null 값으로 Constructor로 ApiInfo를 생성한다")
        void success_nullValues() {
            // when
            ApiInfo apiInfo = new ApiInfo(null, null, null, null);

            // then
            assertThat(apiInfo.id()).isNull();
            assertThat(apiInfo.domain()).isNull();
            assertThat(apiInfo.httpMethod()).isNull();
            assertThat(apiInfo.uriPattern()).isNull();
        }
    }

    @Nested
    @DisplayName("[getPathVariable] URI에서 경로 변수를 추출하는 메서드")
    class Describe_getPathVariable {

        @Test
        @DisplayName("[success] 단일 경로 변수를 추출한다")
        void success_singlePathVariable() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .uriPattern("/api/users/{id}")
                .build();
            String uri = "/api/users/123";

            // when
            String result = apiInfo.getPathVariable(uri);

            // then
            assertThat(result).isEqualTo("123");
        }

        @Test
        @DisplayName("[success] 여러 경로 변수를 추출한다")
        void success_multiplePathVariables() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .uriPattern("/api/users/{userId}/posts/{postId}")
                .build();
            String uri = "/api/users/123/posts/456";

            // when
            String result = apiInfo.getPathVariable(uri);

            // then
            assertThat(result).isEqualTo("123/posts/456");
        }

        @Test
        @DisplayName("[success] 경로 변수가 없으면 빈 문자열을 반환한다")
        void success_noPathVariable() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .uriPattern("/api/users")
                .build();
            String uri = "/api/users";

            // when
            String result = apiInfo.getPathVariable(uri);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] 매칭되지 않는 URI는 빈 문자열을 반환한다")
        void success_unmatchedUri() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .uriPattern("/api/users/{id}")
                .build();
            String uri = "/api/posts/123";

            // when
            String result = apiInfo.getPathVariable(uri);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] null uriPattern은 예외를 처리하고 빈 문자열을 반환한다")
        void success_nullUriPattern() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .uriPattern(null)
                .build();
            String uri = "/api/users/123";

            // when
            String result = apiInfo.getPathVariable(uri);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] null uri는 예외를 처리하고 빈 문자열을 반환한다")
        void success_nullUri() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .uriPattern("/api/users/{id}")
                .build();

            // when
            String result = apiInfo.getPathVariable(null);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] 와일드카드 패턴에서 경로를 추출한다")
        void success_wildcardPattern() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .uriPattern("/api/**")
                .build();
            String uri = "/api/users/123/posts/456";

            // when
            String result = apiInfo.getPathVariable(uri);

            // then
            assertThat(result).isEqualTo("users/123/posts/456");
        }

        @Test
        @DisplayName("[success] 복잡한 패턴에서 경로 변수를 추출한다")
        void success_complexPattern() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .uriPattern("/api/{version}/users/{id}")
                .build();
            String uri = "/api/v1/users/123";

            // when
            String result = apiInfo.getPathVariable(uri);

            // then
            assertThat(result).contains("v1");
            assertThat(result).contains("123");
        }

        @Test
        @DisplayName("[success] 숫자가 아닌 경로 변수를 추출한다")
        void success_nonNumericPathVariable() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .uriPattern("/api/users/{username}")
                .build();
            String uri = "/api/users/john-doe";

            // when
            String result = apiInfo.getPathVariable(uri);

            // then
            assertThat(result).isEqualTo("john-doe");
        }

        @Test
        @DisplayName("[success] 특수 문자를 포함한 경로 변수를 추출한다")
        void success_specialCharacters() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .uriPattern("/api/users/{email}")
                .build();
            String uri = "/api/users/test@example.com";

            // when
            String result = apiInfo.getPathVariable(uri);

            // then
            assertThat(result).isEqualTo("test@example.com");
        }
    }

    @Nested
    @DisplayName("[accessor] Record accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] id()로 id를 조회한다")
        void success_id() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .id(1L)
                .build();

            // when
            Long id = apiInfo.id();

            // then
            assertThat(id).isEqualTo(1L);
        }

        @Test
        @DisplayName("[success] domain()으로 domain을 조회한다")
        void success_domain() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .domain("example.com")
                .build();

            // when
            String domain = apiInfo.domain();

            // then
            assertThat(domain).isEqualTo("example.com");
        }

        @Test
        @DisplayName("[success] httpMethod()로 httpMethod를 조회한다")
        void success_httpMethod() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .httpMethod("GET")
                .build();

            // when
            String httpMethod = apiInfo.httpMethod();

            // then
            assertThat(httpMethod).isEqualTo("GET");
        }

        @Test
        @DisplayName("[success] uriPattern()으로 uriPattern을 조회한다")
        void success_uriPattern() {
            // given
            ApiInfo apiInfo = ApiInfo.builder()
                .uriPattern("/api/users/{id}")
                .build();

            // when
            String uriPattern = apiInfo.uriPattern();

            // then
            assertThat(uriPattern).isEqualTo("/api/users/{id}");
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] ApiInfo는 불변 객체이다")
        void success() {
            // given
            ApiInfo apiInfo1 = new ApiInfo(1L, "example.com", "GET", "/api/users/{id}");
            ApiInfo apiInfo2 = new ApiInfo(1L, "example.com", "GET", "/api/users/{id}");

            // when & then
            assertThat(apiInfo1).isEqualTo(apiInfo2);
            assertThat(apiInfo1.hashCode()).isEqualTo(apiInfo2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 ApiInfo는 동등하지 않다")
        void success_notEqual() {
            // given
            ApiInfo apiInfo1 = new ApiInfo(1L, "example.com", "GET", "/api/users/{id}");
            ApiInfo apiInfo2 = new ApiInfo(2L, "example.com", "GET", "/api/users/{id}");

            // when & then
            assertThat(apiInfo1).isNotEqualTo(apiInfo2);
        }

        @Test
        @DisplayName("[success] 다른 httpMethod를 가진 ApiInfo는 동등하지 않다")
        void success_differentHttpMethod() {
            // given
            ApiInfo apiInfo1 = new ApiInfo(1L, "example.com", "GET", "/api/users/{id}");
            ApiInfo apiInfo2 = new ApiInfo(1L, "example.com", "POST", "/api/users/{id}");

            // when & then
            assertThat(apiInfo1).isNotEqualTo(apiInfo2);
        }
    }

    @Nested
    @DisplayName("[toString] Record toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            ApiInfo apiInfo = new ApiInfo(
                1L,
                "example.com",
                "GET",
                "/api/users/{id}"
            );

            // when
            String result = apiInfo.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("ApiInfo");
            assertThat(result).contains("1");
            assertThat(result).contains("example.com");
            assertThat(result).contains("GET");
            assertThat(result).contains("/api/users/{id}");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 toString()을 반환한다")
        void success_nullValues() {
            // given
            ApiInfo apiInfo = new ApiInfo(null, null, null, null);

            // when
            String result = apiInfo.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("ApiInfo");
            assertThat(result).contains("null");
        }
    }
}
