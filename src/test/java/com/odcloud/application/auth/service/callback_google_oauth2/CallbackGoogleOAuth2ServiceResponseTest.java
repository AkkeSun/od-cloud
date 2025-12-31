package com.odcloud.application.auth.service.callback_google_oauth2;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.adapter.out.client.google.GoogleTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CallbackGoogleOAuth2ServiceResponseTest {

    @Nested
    @DisplayName("[of] GoogleTokenResponse를 ServiceResponse로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] GoogleTokenResponse를 ServiceResponse로 변환한다")
        void success() {
            // given
            GoogleTokenResponse tokenResponse = new GoogleTokenResponse(
                "google-access-token-123",
                "google-id-token-456",
                "google-refresh-token-789",
                "openid profile email",
                "Bearer",
                3600L
            );

            // when
            CallbackGoogleOAuth2ServiceResponse serviceResponse =
                CallbackGoogleOAuth2ServiceResponse.of(tokenResponse);

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(serviceResponse.googleAccessToken()).isEqualTo(
                "Bearer google-access-token-123");
        }
    }

    @Nested
    @DisplayName("[constructor] ServiceResponse 생성자 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] googleAccessToken으로 ServiceResponse를 생성한다")
        void success() {
            // when
            CallbackGoogleOAuth2ServiceResponse serviceResponse =
                new CallbackGoogleOAuth2ServiceResponse("google-access-token-123");

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(serviceResponse.googleAccessToken()).isEqualTo("google-access-token-123");
        }

        @Test
        @DisplayName("[success] null 값으로 ServiceResponse를 생성한다")
        void success_nullValue() {
            // when
            CallbackGoogleOAuth2ServiceResponse serviceResponse =
                new CallbackGoogleOAuth2ServiceResponse(null);

            // then
            assertThat(serviceResponse.googleAccessToken()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열로 ServiceResponse를 생성한다")
        void success_emptyString() {
            // when
            CallbackGoogleOAuth2ServiceResponse serviceResponse =
                new CallbackGoogleOAuth2ServiceResponse("");

            // then
            assertThat(serviceResponse.googleAccessToken()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] ServiceResponse는 불변 객체이다")
        void success() {
            // given
            CallbackGoogleOAuth2ServiceResponse serviceResponse1 =
                new CallbackGoogleOAuth2ServiceResponse("google-access-token-123");

            CallbackGoogleOAuth2ServiceResponse serviceResponse2 =
                new CallbackGoogleOAuth2ServiceResponse("google-access-token-123");

            // when & then
            assertThat(serviceResponse1).isEqualTo(serviceResponse2);
            assertThat(serviceResponse1.hashCode()).isEqualTo(serviceResponse2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 ServiceResponse는 동등하지 않다")
        void success_notEqual() {
            // given
            CallbackGoogleOAuth2ServiceResponse serviceResponse1 =
                new CallbackGoogleOAuth2ServiceResponse("google-access-token-1");

            CallbackGoogleOAuth2ServiceResponse serviceResponse2 =
                new CallbackGoogleOAuth2ServiceResponse("google-access-token-2");

            // when & then
            assertThat(serviceResponse1).isNotEqualTo(serviceResponse2);
        }
    }

    @Nested
    @DisplayName("[accessor] ServiceResponse accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] googleAccessToken()으로 값을 조회한다")
        void success() {
            // given
            CallbackGoogleOAuth2ServiceResponse serviceResponse =
                new CallbackGoogleOAuth2ServiceResponse("test-token");

            // when
            String googleAccessToken = serviceResponse.googleAccessToken();

            // then
            assertThat(googleAccessToken).isEqualTo("test-token");
        }
    }

    @Nested
    @DisplayName("[toString] ServiceResponse toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            CallbackGoogleOAuth2ServiceResponse serviceResponse =
                new CallbackGoogleOAuth2ServiceResponse("test-token");

            // when
            String result = serviceResponse.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("CallbackGoogleOAuth2ServiceResponse");
            assertThat(result).contains("test-token");
        }
    }
}
