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
            assertThat(response.googleAccessToken()).isEqualTo("Bearer fake-access-token");
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
            assertThat(response.googleAccessToken()).isEqualTo("Bearer custom-access-token");
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
}