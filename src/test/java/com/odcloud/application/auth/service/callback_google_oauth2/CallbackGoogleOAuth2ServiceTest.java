package com.odcloud.application.auth.service.callback_google_oauth2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.adapter.out.client.google.GoogleTokenResponse;
import com.odcloud.fakeClass.FakeGoogleOAuth2Port;
import com.odcloud.fakeClass.FakeProfileConstant;
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
        callbackGoogleOAuth2Service = new CallbackGoogleOAuth2Service(
            fakeGoogleOAuth2Port,
            FakeProfileConstant.create()
        );
    }

    @Nested
    @DisplayName("[callback] Google OAuth2 콜백 처리")
    class Describe_callback {

        @Test
        @DisplayName("[success] 정상적으로 googleAccessToken과 redirectUri를 반환한다")
        void success() {
            // when
            CallbackGoogleOAuth2ServiceResponse response =
                callbackGoogleOAuth2Service.callback("test-authorization-code");

            // then
            assertThat(response.googleAccessToken()).isEqualTo("Bearer fake-access-token");
            assertThat(response.redirectUri()).contains("http://localhost:3000/auth/callback");
            assertThat(response.redirectUri()).contains("googleAccessToken=");
        }

        @Test
        @DisplayName("[success] 커스텀 토큰 응답으로 정상 처리한다")
        void success_customTokenResponse() {
            // given
            fakeGoogleOAuth2Port.mockTokenResponse = new GoogleTokenResponse(
                "custom-access-token",
                "custom-id-token",
                "custom-refresh-token",
                "openid profile",
                "Bearer",
                7200L
            );

            // when
            CallbackGoogleOAuth2ServiceResponse response =
                callbackGoogleOAuth2Service.callback("test-code");

            // then
            assertThat(response.googleAccessToken()).isEqualTo("Bearer custom-access-token");
            assertThat(response.redirectUri()).contains("http://localhost:3000/auth/callback");
        }

        @Test
        @DisplayName("[failure] GoogleOAuth2Port에서 예외 발생 시 예외를 전파한다")
        void failure_portThrowsException() {
            // given
            fakeGoogleOAuth2Port.shouldThrowExceptionOnGetToken = true;

            // when & then
            assertThatThrownBy(() -> callbackGoogleOAuth2Service.callback("invalid-code"))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }
    }
}
