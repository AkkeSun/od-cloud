package com.odcloud.application.service.callback_google_oauth2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.adapter.out.client.google.GoogleTokenResponse;
import com.odcloud.fakeClass.FakeGoogleOAuth2Port;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CallbackGoogleOAuth2ServiceTest {

    private FakeGoogleOAuth2Port fakeGoogleOAuth2Port;
    private CallbackGoogleOAuth2Service callbackGoogleOAuth2Service;

    @BeforeEach
    void setUp() {
        fakeGoogleOAuth2Port = new FakeGoogleOAuth2Port();
        callbackGoogleOAuth2Service = new CallbackGoogleOAuth2Service(fakeGoogleOAuth2Port);
    }

    @Nested
    @DisplayName("[callback] Google OAuth2 콜백 처리")
    class Describe_callback {

        @Test
        @DisplayName("[success] 정상적으로 authorization code를 access token으로 교환한다")
        void success() {
            // given
            String code = "test-authorization-code";

            // when
            CallbackGoogleOAuth2ServiceResponse response = callbackGoogleOAuth2Service.callback(code);

            // then
            assertThat(response).isNotNull();
            assertThat(response.googleAccessToken()).isEqualTo("fake-access-token");
        }

        @Test
        @DisplayName("[success] 커스텀 토큰 응답으로 정상 처리한다")
        void success_customTokenResponse() {
            // given
            String code = "test-code";
            GoogleTokenResponse customTokenResponse = new GoogleTokenResponse(
                "custom-access-token",
                "custom-id-token",
                "custom-refresh-token",
                "openid profile",
                "Bearer",
                7200L
            );
            fakeGoogleOAuth2Port.mockTokenResponse = customTokenResponse;

            // when
            CallbackGoogleOAuth2ServiceResponse response = callbackGoogleOAuth2Service.callback(code);

            // then
            assertThat(response).isNotNull();
            assertThat(response.googleAccessToken()).isEqualTo("custom-access-token");
        }

        @Test
        @DisplayName("[failure] GoogleOAuth2Port에서 예외 발생 시 예외를 전파한다")
        void failure_portThrowsException() {
            // given
            String code = "invalid-code";
            fakeGoogleOAuth2Port.shouldThrowExceptionOnGetToken = true;

            // when & then
            assertThatThrownBy(() -> callbackGoogleOAuth2Service.callback(code))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }

        @Test
        @DisplayName("[success] 빈 code로도 호출 가능하다")
        void success_emptyCode() {
            // given
            String code = "";

            // when
            CallbackGoogleOAuth2ServiceResponse response = callbackGoogleOAuth2Service.callback(code);

            // then
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("[success] null access token을 가진 응답도 처리한다")
        void success_nullAccessToken() {
            // given
            String code = "test-code";
            GoogleTokenResponse nullTokenResponse = new GoogleTokenResponse(
                null,
                "id-token",
                "refresh-token",
                "scope",
                "Bearer",
                3600L
            );
            fakeGoogleOAuth2Port.mockTokenResponse = nullTokenResponse;

            // when
            CallbackGoogleOAuth2ServiceResponse response = callbackGoogleOAuth2Service.callback(code);

            // then
            assertThat(response).isNotNull();
            assertThat(response.googleAccessToken()).isNull();
        }

        @Test
        @DisplayName("[success] 긴 authorization code도 정상 처리한다")
        void success_longCode() {
            // given
            String longCode = "a".repeat(1000);

            // when
            CallbackGoogleOAuth2ServiceResponse response = callbackGoogleOAuth2Service.callback(longCode);

            // then
            assertThat(response).isNotNull();
            assertThat(response.googleAccessToken()).isNotNull();
        }
    }

    @Nested
    @DisplayName("[integration] 여러 시나리오 통합 테스트")
    class Describe_integration {

        @Test
        @DisplayName("[success] 연속된 여러 callback 호출이 정상 동작한다")
        void success_multipleCalls() {
            // when
            CallbackGoogleOAuth2ServiceResponse response1 = callbackGoogleOAuth2Service.callback("code1");
            CallbackGoogleOAuth2ServiceResponse response2 = callbackGoogleOAuth2Service.callback("code2");
            CallbackGoogleOAuth2ServiceResponse response3 = callbackGoogleOAuth2Service.callback("code3");

            // then
            assertThat(response1).isNotNull();
            assertThat(response2).isNotNull();
            assertThat(response3).isNotNull();
        }

        @Test
        @DisplayName("[success] 예외 발생 후에도 정상 호출이 가능하다")
        void success_afterException() {
            // given
            fakeGoogleOAuth2Port.shouldThrowExceptionOnGetToken = true;

            // when - 첫 번째 호출은 실패
            assertThatThrownBy(() -> callbackGoogleOAuth2Service.callback("invalid-code"))
                .isInstanceOf(CustomBusinessException.class);

            // then - 두 번째 호출은 성공
            fakeGoogleOAuth2Port.shouldThrowExceptionOnGetToken = false;
            CallbackGoogleOAuth2ServiceResponse response = callbackGoogleOAuth2Service.callback("valid-code");
            assertThat(response).isNotNull();
            assertThat(response.googleAccessToken()).isEqualTo("fake-access-token");
        }

        @Test
        @DisplayName("[success] 다양한 토큰 응답 형태를 처리한다")
        void success_variousTokenResponses() {
            // 첫 번째: 완전한 응답
            GoogleTokenResponse fullResponse = new GoogleTokenResponse(
                "full-access-token",
                "full-id-token",
                "full-refresh-token",
                "openid profile email",
                "Bearer",
                3600L
            );
            fakeGoogleOAuth2Port.mockTokenResponse = fullResponse;
            CallbackGoogleOAuth2ServiceResponse response1 = callbackGoogleOAuth2Service.callback("code1");
            assertThat(response1.googleAccessToken()).isEqualTo("full-access-token");

            // 두 번째: 최소한의 응답
            GoogleTokenResponse minimalResponse = new GoogleTokenResponse(
                "minimal-access-token",
                null,
                null,
                null,
                null,
                null
            );
            fakeGoogleOAuth2Port.mockTokenResponse = minimalResponse;
            CallbackGoogleOAuth2ServiceResponse response2 = callbackGoogleOAuth2Service.callback("code2");
            assertThat(response2.googleAccessToken()).isEqualTo("minimal-access-token");

            // 세 번째: null access token
            GoogleTokenResponse nullAccessResponse = new GoogleTokenResponse(
                null,
                "id-token",
                null,
                null,
                null,
                null
            );
            fakeGoogleOAuth2Port.mockTokenResponse = nullAccessResponse;
            CallbackGoogleOAuth2ServiceResponse response3 = callbackGoogleOAuth2Service.callback("code3");
            assertThat(response3.googleAccessToken()).isNull();
        }
    }

    @Nested
    @DisplayName("[boundary] 경계값 테스트")
    class Describe_boundary {

        @Test
        @DisplayName("[success] 매우 짧은 code 처리")
        void success_veryShortCode() {
            // given
            String shortCode = "a";

            // when
            CallbackGoogleOAuth2ServiceResponse response = callbackGoogleOAuth2Service.callback(shortCode);

            // then
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("[success] 특수문자가 포함된 code 처리")
        void success_specialCharactersInCode() {
            // given
            String specialCode = "test-code!@#$%^&*()_+-=[]{}|;:',.<>?/~`";

            // when
            CallbackGoogleOAuth2ServiceResponse response = callbackGoogleOAuth2Service.callback(specialCode);

            // then
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("[success] 유니코드 문자가 포함된 code 처리")
        void success_unicodeCharactersInCode() {
            // given
            String unicodeCode = "한글코드-日本語-中文-🎉";

            // when
            CallbackGoogleOAuth2ServiceResponse response = callbackGoogleOAuth2Service.callback(unicodeCode);

            // then
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("[success] 공백이 포함된 code 처리")
        void success_codeWithSpaces() {
            // given
            String codeWithSpaces = "code with spaces";

            // when
            CallbackGoogleOAuth2ServiceResponse response = callbackGoogleOAuth2Service.callback(codeWithSpaces);

            // then
            assertThat(response).isNotNull();
        }
    }
}