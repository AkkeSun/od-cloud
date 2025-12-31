package com.odcloud.adapter.in.controller.auth.callback_google_oauth2;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.application.auth.service.callback_google_oauth2.CallbackGoogleOAuth2ServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CallbackGoogleOAuth2ResponseTest {

    @Nested
    @DisplayName("[of] ServiceResponse를 Response로 변환하는 정적 팩토리 메서드")
    class Describe_of {

        @Test
        @DisplayName("[success] ServiceResponse를 Response로 변환한다")
        void success() {
            // given
            CallbackGoogleOAuth2ServiceResponse serviceResponse =
                new CallbackGoogleOAuth2ServiceResponse("google-access-token-123");

            // when
            CallbackGoogleOAuth2Response response =
                CallbackGoogleOAuth2Response.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.googleAccessToken()).isEqualTo("google-access-token-123");
        }

        @Test
        @DisplayName("[success] null 값이 포함된 ServiceResponse를 Response로 변환한다")
        void success_nullValue() {
            // given
            CallbackGoogleOAuth2ServiceResponse serviceResponse =
                new CallbackGoogleOAuth2ServiceResponse(null);

            // when
            CallbackGoogleOAuth2Response response =
                CallbackGoogleOAuth2Response.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.googleAccessToken()).isNull();
        }

        @Test
        @DisplayName("[success] 빈 문자열이 포함된 ServiceResponse를 Response로 변환한다")
        void success_emptyString() {
            // given
            CallbackGoogleOAuth2ServiceResponse serviceResponse =
                new CallbackGoogleOAuth2ServiceResponse("");

            // when
            CallbackGoogleOAuth2Response response =
                CallbackGoogleOAuth2Response.of(serviceResponse);

            // then
            assertThat(response).isNotNull();
            assertThat(response.googleAccessToken()).isEmpty();
        }
    }

    @Nested
    @DisplayName("[constructor] Response 생성자 테스트")
    class Describe_constructor {

        @Test
        @DisplayName("[success] googleAccessToken으로 Response를 생성한다")
        void success() {
            // when
            CallbackGoogleOAuth2Response response =
                new CallbackGoogleOAuth2Response("google-access-token-123");

            // then
            assertThat(response).isNotNull();
            assertThat(response.googleAccessToken()).isEqualTo("google-access-token-123");
        }

        @Test
        @DisplayName("[success] null 값으로 Response를 생성한다")
        void success_nullValue() {
            // when
            CallbackGoogleOAuth2Response response =
                new CallbackGoogleOAuth2Response(null);

            // then
            assertThat(response.googleAccessToken()).isNull();
        }
    }

    @Nested
    @DisplayName("[record] Record 불변성 테스트")
    class Describe_immutability {

        @Test
        @DisplayName("[success] Response는 불변 객체이다")
        void success() {
            // given
            CallbackGoogleOAuth2Response response1 =
                new CallbackGoogleOAuth2Response("google-access-token-123");

            CallbackGoogleOAuth2Response response2 =
                new CallbackGoogleOAuth2Response("google-access-token-123");

            // when & then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[success] 다른 값으로 생성된 Response는 동등하지 않다")
        void success_notEqual() {
            // given
            CallbackGoogleOAuth2Response response1 =
                new CallbackGoogleOAuth2Response("google-access-token-1");

            CallbackGoogleOAuth2Response response2 =
                new CallbackGoogleOAuth2Response("google-access-token-2");

            // when & then
            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("[accessor] Response accessor 메서드 테스트")
    class Describe_accessor {

        @Test
        @DisplayName("[success] googleAccessToken()으로 값을 조회한다")
        void success() {
            // given
            CallbackGoogleOAuth2Response response =
                new CallbackGoogleOAuth2Response("test-token");

            // when
            String googleAccessToken = response.googleAccessToken();

            // then
            assertThat(googleAccessToken).isEqualTo("test-token");
        }
    }

    @Nested
    @DisplayName("[toString] Response toString 메서드 테스트")
    class Describe_toString {

        @Test
        @DisplayName("[success] toString()으로 문자열 표현을 반환한다")
        void success() {
            // given
            CallbackGoogleOAuth2Response response =
                new CallbackGoogleOAuth2Response("test-token");

            // when
            String result = response.toString();

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("CallbackGoogleOAuth2Response");
            assertThat(result).contains("test-token");
        }
    }
}
