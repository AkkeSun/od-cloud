package com.odcloud.adapter.in.controller.webhook.googleplay;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odcloud.application.webhook.port.in.command.GooglePlayNotificationCommand;
import com.odcloud.domain.model.GooglePlayNotificationType;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GooglePlayWebhookRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("[toCommand] Request를 Command로 변환하는 메소드")
    class Describe_toCommand {

        @Nested
        @DisplayName("테스트 알림인 경우")
        class Context_testNotification {

            @Test
            @DisplayName("[success] testNotification이 true인 Command를 반환한다")
            void success() {
                // given
                String jsonData = """
                    {
                        "packageName": "com.odcloud.app",
                        "eventTimeMillis": "1704067200000",
                        "testNotification": {
                            "version": "1.0"
                        }
                    }
                    """;
                String encodedData = Base64.getEncoder().encodeToString(
                    jsonData.getBytes(StandardCharsets.UTF_8));

                GooglePlayWebhookRequest.Message message = new GooglePlayWebhookRequest.Message(
                    encodedData, "msg_123", "2024-01-01T00:00:00Z");
                GooglePlayWebhookRequest request = new GooglePlayWebhookRequest(message,
                    "projects/test/subscriptions/test-sub");

                // when
                GooglePlayNotificationCommand command = request.toCommand();

                // then
                assertThat(command.testNotification()).isTrue();
                assertThat(command.packageName()).isEqualTo("com.odcloud.app");
                assertThat(command.eventTimeMillis()).isEqualTo(1704067200000L);
            }
        }

        @Nested
        @DisplayName("구독 알림인 경우")
        class Context_subscriptionNotification {

            @Test
            @DisplayName("[success] SUBSCRIPTION_RENEWED 알림을 올바르게 파싱한다")
            void success_renewed() {
                // given
                String jsonData = """
                    {
                        "packageName": "com.odcloud.app",
                        "eventTimeMillis": "1704067200000",
                        "subscriptionNotification": {
                            "notificationType": 2,
                            "purchaseToken": "test_token_123",
                            "subscriptionId": "monthly_subscription"
                        }
                    }
                    """;
                String encodedData = Base64.getEncoder().encodeToString(
                    jsonData.getBytes(StandardCharsets.UTF_8));

                GooglePlayWebhookRequest.Message message = new GooglePlayWebhookRequest.Message(
                    encodedData, "msg_456", "2024-01-01T00:00:00Z");
                GooglePlayWebhookRequest request = new GooglePlayWebhookRequest(message,
                    "projects/test/subscriptions/test-sub");

                // when
                GooglePlayNotificationCommand command = request.toCommand();

                // then
                assertThat(command.testNotification()).isFalse();
                assertThat(command.notificationType()).isEqualTo(
                    GooglePlayNotificationType.SUBSCRIPTION_RENEWED);
                assertThat(command.purchaseToken()).isEqualTo("test_token_123");
                assertThat(command.subscriptionId()).isEqualTo("monthly_subscription");
            }

            @Test
            @DisplayName("[success] SUBSCRIPTION_REVOKED 알림을 올바르게 파싱한다")
            void success_revoked() {
                // given
                String jsonData = """
                    {
                        "packageName": "com.odcloud.app",
                        "eventTimeMillis": "1704067200000",
                        "subscriptionNotification": {
                            "notificationType": 12,
                            "purchaseToken": "revoked_token"
                        }
                    }
                    """;
                String encodedData = Base64.getEncoder().encodeToString(
                    jsonData.getBytes(StandardCharsets.UTF_8));

                GooglePlayWebhookRequest.Message message = new GooglePlayWebhookRequest.Message(
                    encodedData, "msg_789", "2024-01-01T00:00:00Z");
                GooglePlayWebhookRequest request = new GooglePlayWebhookRequest(message,
                    "projects/test/subscriptions/test-sub");

                // when
                GooglePlayNotificationCommand command = request.toCommand();

                // then
                assertThat(command.notificationType()).isEqualTo(
                    GooglePlayNotificationType.SUBSCRIPTION_REVOKED);
                assertThat(command.purchaseToken()).isEqualTo("revoked_token");
            }

            @Test
            @DisplayName("[success] SUBSCRIPTION_CANCELED 알림을 올바르게 파싱한다")
            void success_canceled() {
                // given
                String jsonData = """
                    {
                        "packageName": "com.odcloud.app",
                        "eventTimeMillis": "1704067200000",
                        "subscriptionNotification": {
                            "notificationType": 3,
                            "purchaseToken": "canceled_token",
                            "subscriptionId": "yearly_subscription"
                        }
                    }
                    """;
                String encodedData = Base64.getEncoder().encodeToString(
                    jsonData.getBytes(StandardCharsets.UTF_8));

                GooglePlayWebhookRequest.Message message = new GooglePlayWebhookRequest.Message(
                    encodedData, "msg_abc", "2024-01-01T00:00:00Z");
                GooglePlayWebhookRequest request = new GooglePlayWebhookRequest(message,
                    "projects/test/subscriptions/test-sub");

                // when
                GooglePlayNotificationCommand command = request.toCommand();

                // then
                assertThat(command.notificationType()).isEqualTo(
                    GooglePlayNotificationType.SUBSCRIPTION_CANCELED);
            }
        }

        @Nested
        @DisplayName("Voided Purchase 알림인 경우")
        class Context_voidedPurchaseNotification {

            @Test
            @DisplayName("[success] voidedPurchaseNotification을 올바르게 파싱한다")
            void success() {
                // given
                String jsonData = """
                    {
                        "packageName": "com.odcloud.app",
                        "eventTimeMillis": "1704067200000",
                        "voidedPurchaseNotification": {
                            "purchaseToken": "voided_token",
                            "orderId": "GPA.1234-5678-9012",
                            "refundType": 1
                        }
                    }
                    """;
                String encodedData = Base64.getEncoder().encodeToString(
                    jsonData.getBytes(StandardCharsets.UTF_8));

                GooglePlayWebhookRequest.Message message = new GooglePlayWebhookRequest.Message(
                    encodedData, "msg_voided", "2024-01-01T00:00:00Z");
                GooglePlayWebhookRequest request = new GooglePlayWebhookRequest(message,
                    "projects/test/subscriptions/test-sub");

                // when
                GooglePlayNotificationCommand command = request.toCommand();

                // then
                assertThat(command.notificationType()).isEqualTo(
                    GooglePlayNotificationType.SUBSCRIPTION_REVOKED);
                assertThat(command.purchaseToken()).isEqualTo("voided_token");
                assertThat(command.orderId()).isEqualTo("GPA.1234-5678-9012");
                assertThat(command.refundType()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("일회성 상품 알림인 경우")
        class Context_oneTimeProductNotification {

            @Test
            @DisplayName("[success] ONE_TIME_PRODUCT_PURCHASED 알림을 올바르게 파싱한다")
            void success_purchased() {
                // given
                String jsonData = """
                    {
                        "packageName": "com.odcloud.app",
                        "eventTimeMillis": "1704067200000",
                        "oneTimeProductNotification": {
                            "notificationType": 1,
                            "purchaseToken": "one_time_token",
                            "sku": "premium_feature"
                        }
                    }
                    """;
                String encodedData = Base64.getEncoder().encodeToString(
                    jsonData.getBytes(StandardCharsets.UTF_8));

                GooglePlayWebhookRequest.Message message = new GooglePlayWebhookRequest.Message(
                    encodedData, "msg_onetime", "2024-01-01T00:00:00Z");
                GooglePlayWebhookRequest request = new GooglePlayWebhookRequest(message,
                    "projects/test/subscriptions/test-sub");

                // when
                GooglePlayNotificationCommand command = request.toCommand();

                // then
                assertThat(command.notificationType()).isEqualTo(
                    GooglePlayNotificationType.ONE_TIME_PRODUCT_PURCHASED);
                assertThat(command.purchaseToken()).isEqualTo("one_time_token");
                assertThat(command.subscriptionId()).isEqualTo("premium_feature");
            }
        }

        @Nested
        @DisplayName("유효하지 않은 요청인 경우")
        class Context_invalidRequest {

            @Test
            @DisplayName("[error] message가 null이면 예외를 발생시킨다")
            void error_nullMessage() {
                // given
                GooglePlayWebhookRequest request = new GooglePlayWebhookRequest(null,
                    "projects/test/subscriptions/test-sub");

                // when & then
                assertThatThrownBy(request::toCommand)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("missing message data");
            }

            @Test
            @DisplayName("[error] message.data가 null이면 예외를 발생시킨다")
            void error_nullData() {
                // given
                GooglePlayWebhookRequest.Message message = new GooglePlayWebhookRequest.Message(
                    null, "msg_123", "2024-01-01T00:00:00Z");
                GooglePlayWebhookRequest request = new GooglePlayWebhookRequest(message,
                    "projects/test/subscriptions/test-sub");

                // when & then
                assertThatThrownBy(request::toCommand)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("missing message data");
            }

            @Test
            @DisplayName("[error] 알 수 없는 알림 타입이면 예외를 발생시킨다")
            void error_unknownNotificationType() {
                // given
                String jsonData = """
                    {
                        "packageName": "com.odcloud.app",
                        "eventTimeMillis": "1704067200000"
                    }
                    """;
                String encodedData = Base64.getEncoder().encodeToString(
                    jsonData.getBytes(StandardCharsets.UTF_8));

                GooglePlayWebhookRequest.Message message = new GooglePlayWebhookRequest.Message(
                    encodedData, "msg_unknown", "2024-01-01T00:00:00Z");
                GooglePlayWebhookRequest request = new GooglePlayWebhookRequest(message,
                    "projects/test/subscriptions/test-sub");

                // when & then
                assertThatThrownBy(request::toCommand)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unknown Google Play notification type");
            }
        }
    }

    @Nested
    @DisplayName("[JSON 직렬화/역직렬화]")
    class Describe_jsonSerialization {

        @Test
        @DisplayName("[success] Google Play Pub/Sub 형식의 JSON을 올바르게 역직렬화한다")
        void success_deserialize() throws Exception {
            // given
            String json = """
                {
                    "message": {
                        "data": "eyJwYWNrYWdlTmFtZSI6ImNvbS5vZGNsb3VkLmFwcCJ9",
                        "messageId": "123456789",
                        "publishTime": "2024-01-01T00:00:00Z"
                    },
                    "subscription": "projects/my-project/subscriptions/my-sub"
                }
                """;

            // when
            GooglePlayWebhookRequest request = objectMapper.readValue(json,
                GooglePlayWebhookRequest.class);

            // then
            assertThat(request.message()).isNotNull();
            assertThat(request.message().data()).isEqualTo(
                "eyJwYWNrYWdlTmFtZSI6ImNvbS5vZGNsb3VkLmFwcCJ9");
            assertThat(request.message().messageId()).isEqualTo("123456789");
            assertThat(request.subscription()).isEqualTo(
                "projects/my-project/subscriptions/my-sub");
        }
    }
}
