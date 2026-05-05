package com.odcloud.application.auth.service.reissue_token;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReissueTokenResponseTest {

    @Nested
    @DisplayName("[constructor] Constructor 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] Constructor로 ReissueTokenResponse를 생성한다")
        void success() {
            // when
            ReissueTokenResponse response = new ReissueTokenResponse(
                "access-token-123",
                "refresh-token-456"
            );

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("access-token-123");
            assertThat(response.refreshToken()).isEqualTo("refresh-token-456");
        }

        @Test
        @DisplayName("[success] null 값으로 Constructor로 ReissueTokenResponse를 생성한다")
        void success_nullValues() {
            // when
            ReissueTokenResponse response = new ReissueTokenResponse(null, null);

            // then
            assertThat(response.accessToken()).isNull();
            assertThat(response.refreshToken()).isNull();
        }
    }

    @Nested
    @DisplayName("[builder] Builder 테스트")
    class Describe_builder {

        @Test
        @DisplayName("[success] Builder로 ReissueTokenResponse를 생성한다")
        void success() {
            // when
            ReissueTokenResponse response = ReissueTokenResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("access-token-123");
            assertThat(response.refreshToken()).isEqualTo("refresh-token-456");
        }

        @Test
        @DisplayName("[success] Builder로 null 값을 가진 ReissueTokenResponse를 생성한다")
        void success_nullValues() {
            // when
            ReissueTokenResponse response = ReissueTokenResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .build();

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isNull();
            assertThat(response.refreshToken()).isNull();
        }
    }

    @Nested
    @DisplayName("[accessor] Record accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] accessToken()으로 accessToken을 조회한다")
        void success_accessToken() {
            // given
            ReissueTokenResponse response = ReissueTokenResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .build();

            // when
            String accessToken = response.accessToken();

            // then
            assertThat(accessToken).isEqualTo("access-token-123");
        }

        @Test
        @DisplayName("[success] refreshToken()으로 refreshToken을 조회한다")
        void success_refreshToken() {
            // given
            ReissueTokenResponse response = ReissueTokenResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .build();

            // when
            String refreshToken = response.refreshToken();

            // then
            assertThat(refreshToken).isEqualTo("refresh-token-456");
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] ReissueTokenResponse는 불변 객체이다")
        void success() {
            // given
            ReissueTokenResponse response1 = new ReissueTokenResponse(
                "access-token-123",
                "refresh-token-456"
            );
            ReissueTokenResponse response2 = new ReissueTokenResponse(
                "access-token-123",
                "refresh-token-456"
            );

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 accessToken을 가진 ReissueTokenResponse는 동등하지 않다")
        void success_differentAccessToken() {
            // given
            ReissueTokenResponse response1 = new ReissueTokenResponse(
                "access-token-123",
                "refresh-token-456"
            );
            ReissueTokenResponse response2 = new ReissueTokenResponse(
                "access-token-789",
                "refresh-token-456"
            );

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        @DisplayName("[success] 다른 refreshToken을 가진 ReissueTokenResponse는 동등하지 않다")
        void success_differentRefreshToken() {
            // given
            ReissueTokenResponse response1 = new ReissueTokenResponse(
                "access-token-123",
                "refresh-token-456"
            );
            ReissueTokenResponse response2 = new ReissueTokenResponse(
                "access-token-123",
                "refresh-token-789"
            );

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("[toString] Record toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            ReissueTokenResponse response = new ReissueTokenResponse(
                "access-token-123",
                "refresh-token-456"
            );

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("ReissueTokenResponse");
            assertThat(result).contains("access-token-123");
            assertThat(result).contains("refresh-token-456");
        }

        @Test
        @DisplayName("[success] null 값을 포함한 toString()을 반환한다")
        void success_nullValues() {
            // given
            ReissueTokenResponse response = new ReissueTokenResponse(null, null);

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("ReissueTokenResponse");
            assertThat(result).contains("null");
        }
    }
}
