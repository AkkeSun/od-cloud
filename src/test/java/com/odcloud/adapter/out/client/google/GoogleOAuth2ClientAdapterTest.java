package com.odcloud.adapter.out.client.google;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.odcloud.application.auth.port.out.GoogleUserInfo;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.constant.ProfileConstant.GoogleOAuth2;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GoogleOAuth2ClientAdapterTest {

    private GoogleOAuth2ClientAdapter adapter;

    static MockWebServer mockWebServer;

    @BeforeEach
     void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        adapter = new GoogleOAuth2ClientAdapter(ProfileConstant.builder()
            .googleOAuth2(GoogleOAuth2.builder()
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .redirectUri("test-redirect-uri")
                .tokenApi(mockWebServer.url("/").toString())
                .userInfoApi(mockWebServer.url("/").toString())
                .build())
            .build());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("[getToken] Google OAuth2 토큰 발급")
    class Describe_getToken {

        @Test
        @DisplayName("[success] 정상적으로 액세스 토큰을 발급받는다")
        void success()  {
            // given
            String code = "test-authorization-code";
            String responseBody = """
                {
                    "access_token": "test-access-token",
                    "id_token": "test-id-token",
                    "refresh_token": "test-refresh-token",
                    "scope": "openid profile email",
                    "token_type": "Bearer",
                    "expires_in": 3600
                }
                """;

            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

            // when
            String accessToken = adapter.getToken(code);

            // then
            assertThat(accessToken).isEqualTo("test-access-token");
        }

        @Test
        @DisplayName("[failure] 400 Bad Request 응답 시 예외가 발생한다")
        void failure_badRequest() {
            // given
            String code = "invalid-code";
            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\": \"invalid_grant\"}")
                .addHeader("Content-Type", "application/json"));

            // when & then
            assertThatThrownBy(() -> adapter.getToken(code))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }

        @Test
        @DisplayName("[failure] 401 Unauthorized 응답 시 예외가 발생한다")
        void failure_unauthorized() {
            // given
            String code = "unauthorized-code";
            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\": \"unauthorized\"}")
                .addHeader("Content-Type", "application/json"));

            // when & then
            assertThatThrownBy(() -> adapter.getToken(code))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }

        @Test
        @DisplayName("[failure] 500 Internal Server Error 응답 시 예외가 발생한다")
        void failure_internalServerError() {
            // given
            String code = "test-code";
            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\": \"internal_server_error\"}")
                .addHeader("Content-Type", "application/json"));

            // when & then
            assertThatThrownBy(() -> adapter.getToken(code))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }

        @Test
        @DisplayName("[failure] 잘못된 JSON 응답 시 예외가 발생한다")
        void failure_invalidJson() {
            // given
            String code = "test-code";
            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("invalid json")
                .addHeader("Content-Type", "application/json"));

            // when & then
            assertThatThrownBy(() -> adapter.getToken(code))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }
    }

    @Nested
    @DisplayName("[getUserInfo] Google OAuth2 사용자 정보 조회")
    class Describe_getUserInfo {

        @Test
        @DisplayName("[success] 정상적으로 사용자 정보를 조회한다")
        void success() {
            // given
            String accessToken = "Bearer test-access-token";
            String responseBody = """
                {
                    "sub": "123456789",
                    "name": "홍길동",
                    "given_name": "길동",
                    "picture": "https://example.com/photo.jpg",
                    "email": "test@example.com",
                    "email_verified": true
                }
                """;

            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

            // when
            GoogleUserInfo userInfo = adapter.getUserInfo(accessToken);

            // then
            assertThat(userInfo).isNotNull();
            assertThat(userInfo.email()).isEqualTo("test@example.com");
            assertThat(userInfo.name()).isEqualTo("홍길동");
            assertThat(userInfo.picture()).isEqualTo("https://example.com/photo.jpg");
        }

        @Test
        @DisplayName("[failure] 401 Unauthorized 응답 시 예외가 발생한다")
        void failure_unauthorized() {
            // given
            String accessToken = "Bearer invalid-token";
            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\": \"unauthorized\"}")
                .addHeader("Content-Type", "application/json"));

            // when & then
            assertThatThrownBy(() -> adapter.getUserInfo(accessToken))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }

        @Test
        @DisplayName("[failure] 500 Internal Server Error 응답 시 예외가 발생한다")
        void failure_internalServerError() {
            // given
            String accessToken = "Bearer test-token";
            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\": \"internal_server_error\"}")
                .addHeader("Content-Type", "application/json"));

            // when & then
            assertThatThrownBy(() -> adapter.getUserInfo(accessToken))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }

        @Test
        @DisplayName("[failure] 잘못된 JSON 응답 시 예외가 발생한다")
        void failure_invalidJson() {
            // given
            String accessToken = "Bearer test-token";
            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("invalid json")
                .addHeader("Content-Type", "application/json"));

            // when & then
            assertThatThrownBy(() -> adapter.getUserInfo(accessToken))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.Business_GOOGLE_USER_INFO_ERROR);
        }

        @Test
        @DisplayName("[success] 일부 필드가 null인 경우에도 정상 처리한다")
        void success_partialNullFields() {
            // given
            String accessToken = "Bearer test-token";
            String responseBody = """
                {
                    "sub": "123456789",
                    "email": "test@example.com",
                    "email_verified": true
                }
                """;

            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

            // when
            GoogleUserInfo userInfo = adapter.getUserInfo(accessToken);

            // then
            assertThat(userInfo).isNotNull();
            assertThat(userInfo.email()).isEqualTo("test@example.com");
            assertThat(userInfo.name()).isNull();
            assertThat(userInfo.picture()).isNull();
        }
    }
}
