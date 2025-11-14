package com.odcloud.adapter.in.issue_token;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.service.issue_token.IssueTokenServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class IssueTokenResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            IssueTokenServiceResponse serviceResponse = IssueTokenServiceResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .build();

            // when
            IssueTokenResponse response = IssueTokenResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("access-token-123");
            assertThat(response.refreshToken()).isEqualTo("refresh-token-456");
        }

        @Test
        @DisplayName("[success] null 값이 포함된 ServiceResponse를 Response로 변환한다")
        void success_nullValues() {
            // given
            IssueTokenServiceResponse serviceResponse = IssueTokenServiceResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .build();

            // when
            IssueTokenResponse response = IssueTokenResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isNull();
            assertThat(response.refreshToken()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열이 포함된 ServiceResponse를 Response로 변환한다")
        void success_emptyStrings() {
            // given
            IssueTokenServiceResponse serviceResponse = IssueTokenServiceResponse.builder()
                .accessToken("")
                .refreshToken("")
                .build();

            // when
            IssueTokenResponse response = IssueTokenResponse.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEmpty();
            assertThat(response.refreshToken()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 패턴 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 Response를 생성한다")
        void success() {
            // when
            IssueTokenResponse response = IssueTokenResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("access-token-123");
            assertThat(response.refreshToken()).isEqualTo("refresh-token-456");
        }

        @Test
        @DisplayName("[success] null 값으로 Response를 생성한다")
        void success_nullValues() {
            // when
            IssueTokenResponse response = IssueTokenResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .build();

            // then
            assertThat(response.accessToken()).isNull();
            assertThat(response.refreshToken()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Response는 불변 객체이다")
        void success() {
            // given
            IssueTokenResponse response1 = IssueTokenResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .build();

            IssueTokenResponse response2 = IssueTokenResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .build();

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Response는 동등하지 않다")
        void success_notEqual() {
            // given
            IssueTokenResponse response1 = IssueTokenResponse.builder()
                .accessToken("access-token-1")
                .refreshToken("refresh-token-1")
                .build();

            IssueTokenResponse response2 = IssueTokenResponse.builder()
                .accessToken("access-token-2")
                .refreshToken("refresh-token-2")
                .build();

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("[accessor] Response accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] accessToken()으로 값을 조회한다")
        void success_accessToken() {
            // given
            IssueTokenResponse response = IssueTokenResponse.builder()
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .build();

            // when
            String accessToken = response.accessToken();

            // then
            assertThat(accessToken).isEqualTo("test-access-token");
        }

        @Test
        @DisplayName("[success] refreshToken()으로 값을 조회한다")
        void success_refreshToken() {
            // given
            IssueTokenResponse response = IssueTokenResponse.builder()
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .build();

            // when
            String refreshToken = response.refreshToken();

            // then
            assertThat(refreshToken).isEqualTo("test-refresh-token");
        }
    }

    @Nested
    @DisplayName("[toString] Response toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            IssueTokenResponse response = IssueTokenResponse.builder()
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .build();

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("IssueTokenResponse");
            assertThat(result).contains("test-access-token");
            assertThat(result).contains("test-refresh-token");
        }
    }
}
