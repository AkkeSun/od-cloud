package com.odcloud.adapter.out.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.io.IOException;
import java.time.LocalDateTime;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SlackAdapterTest {

    private MockWebServer mockWebServer;
    private SlackAdapter slackAdapter;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();

        ProfileConstant.Jwt jwtConfig = new ProfileConstant.Jwt(
            300000L,
            1800000L,
            604800000L,
            "test-secret-key"
        );

        ProfileConstant.Slack slackConfig = new ProfileConstant.Slack(
            baseUrl,
            "test-token-12345"
        );

        ProfileConstant profileConstant = new ProfileConstant(
            jwtConfig,
            slackConfig,
            "test-redis-key",
            "test-aes-secret-key"
        );

        slackAdapter = new SlackAdapter(profileConstant);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("[sendMessage] Slack 메시지를 전송하는 메서드")
    class Describe_sendMessage {

        @Test
        @DisplayName("[success] Slack API 호출에 성공한다")
        void success() throws InterruptedException {
            // given
            SlackRequest request = SlackRequest.builder()
                .channel("#monitoring")
                .text("Test message")
                .build();

            mockWebServer.enqueue(new MockResponse()
                .setBody("{\"ok\": true}")
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200));

            // when & then
            assertDoesNotThrow(() -> slackAdapter.sendMessage(request));

            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/chat.postMessage");
            assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("test-token-12345");
            assertThat(recordedRequest.getHeader("Content-Type")).contains("application/json");
        }

        @Test
        @DisplayName("[success] Account 정보로 Slack 메시지를 전송한다")
        void success_withAccount() throws InterruptedException {
            // given
            Account account = Account.builder()
                .username("testuser")
                .name("홍길동")
                .email("test@example.com")
                .password("password123")
                .role("ROLE_USER")
                .twoFactorSecret("SECRET")
                .isAdminApproved(false)
                .regDt(LocalDateTime.now())
                .build();

            SlackRequest request = SlackRequest.ofCreateAccount(account);

            mockWebServer.enqueue(new MockResponse()
                .setBody("{\"ok\": true}")
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200));

            // when
            slackAdapter.sendMessage(request);

            // then
            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            String body = recordedRequest.getBody().readUtf8();

            assertThat(body).contains("\"channel\":\"#monitoring\"");
            assertThat(body).contains("testuser");
            assertThat(body).contains("홍길동");
            assertThat(body).contains("test@example.com");
        }

        @Test
        @DisplayName("[success] Authorization 헤더에 Bearer 토큰이 포함된다")
        void success_authorizationHeader() throws InterruptedException {
            // given
            SlackRequest request = SlackRequest.builder()
                .channel("#alerts")
                .text("Alert message")
                .build();

            mockWebServer.enqueue(new MockResponse()
                .setBody("{\"ok\": true}")
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200));

            // when
            slackAdapter.sendMessage(request);

            // then
            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getHeader("Authorization"))
                .isEqualTo("test-token-12345");
        }

        @Test
        @DisplayName("[error] Slack API가 ok: false를 반환하면 CustomBusinessException을 던진다")
        void error_slackApiFailed() {
            // given
            SlackRequest request = SlackRequest.builder()
                .channel("#monitoring")
                .text("Test message")
                .build();

            mockWebServer.enqueue(new MockResponse()
                .setBody("{\"ok\": false, \"error\": \"channel_not_found\"}")
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200));

            // when & then
            CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> slackAdapter.sendMessage(request)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.Business_SLACK_CLIENT_ERROR);
        }

        @Test
        @DisplayName("[error] Slack API가 ok: false를 반환하면 예외가 발생한다")
        void error_invalidChannel() {
            // given
            SlackRequest request = SlackRequest.builder()
                .channel("#invalid-channel")
                .text("Test message")
                .build();

            mockWebServer.enqueue(new MockResponse()
                .setBody("{\"ok\": false, \"error\": \"invalid_channel\"}")
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200));

            // when & then
            CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> slackAdapter.sendMessage(request)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.Business_SLACK_CLIENT_ERROR);
            assertThat(exception.getErrorCode().getMessage()).isNotEmpty();
        }

        @Test
        @DisplayName("[success] 다양한 채널로 메시지를 전송한다")
        void success_differentChannels() throws InterruptedException {
            // given
            SlackRequest request1 = SlackRequest.builder()
                .channel("#monitoring")
                .text("Monitoring message")
                .build();

            SlackRequest request2 = SlackRequest.builder()
                .channel("#alerts")
                .text("Alert message")
                .build();

            mockWebServer.enqueue(new MockResponse()
                .setBody("{\"ok\": true}")
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200));

            mockWebServer.enqueue(new MockResponse()
                .setBody("{\"ok\": true}")
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200));

            // when
            slackAdapter.sendMessage(request1);
            slackAdapter.sendMessage(request2);

            // then
            RecordedRequest recordedRequest1 = mockWebServer.takeRequest();
            String body1 = recordedRequest1.getBody().readUtf8();
            assertThat(body1).contains("#monitoring");
            assertThat(body1).contains("Monitoring message");

            RecordedRequest recordedRequest2 = mockWebServer.takeRequest();
            String body2 = recordedRequest2.getBody().readUtf8();
            assertThat(body2).contains("#alerts");
            assertThat(body2).contains("Alert message");
        }

        @Test
        @DisplayName("[success] JSON 응답의 ok 필드가 true이면 성공한다")
        void success_okFieldTrue() {
            // given
            SlackRequest request = SlackRequest.builder()
                .channel("#test")
                .text("Test")
                .build();

            mockWebServer.enqueue(new MockResponse()
                .setBody("{\"ok\": true, \"channel\": \"C123456\", \"ts\": \"1234567890.123456\"}")
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200));

            // when & then
            assertDoesNotThrow(() -> slackAdapter.sendMessage(request));
        }
    }
}
