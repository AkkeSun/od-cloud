package com.odcloud.adapter.in.controller.webhook.googleplay;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.webhook.port.in.HandleGooglePlayWebhookUseCase;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class GooglePlayWebhookControllerDocsTest extends RestDocsSupport {

    private final HandleGooglePlayWebhookUseCase useCase = mock(HandleGooglePlayWebhookUseCase.class);
    private final String apiName = "Google Play Webhook API";

    @Override
    protected Object initController() {
        return new GooglePlayWebhookController(useCase);
    }

    @Nested
    @DisplayName("[handleGooglePlayWebhook] Google Play 웹훅을 처리하는 API")
    class Describe_handleGooglePlayWebhook {

        @Test
        @DisplayName("[success] 구독 갱신 알림을 정상 처리한다")
        void success_subscriptionRenewed() throws Exception {
            // given
            String jsonData = createSubscriptionNotificationJson(2, "purchase_token_123",
                "monthly_subscription");
            GooglePlayWebhookRequest request = createRequest(jsonData);

            willDoNothing().given(useCase).handle(any());

            // when & then
            performDocument(request, status().isOk(), "success-renewal", "success");
        }

        @Test
        @DisplayName("[success] 테스트 알림을 정상 처리한다")
        void success_testNotification() throws Exception {
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
            GooglePlayWebhookRequest request = createRequest(jsonData);

            willDoNothing().given(useCase).handle(any());

            // when & then
            performDocument(request, status().isOk(), "success-test", "success");
        }

        @Test
        @DisplayName("[success] 환불 알림을 정상 처리한다")
        void success_subscriptionRevoked() throws Exception {
            // given
            String jsonData = createSubscriptionNotificationJson(12, "revoked_token_456", null);
            GooglePlayWebhookRequest request = createRequest(jsonData);

            willDoNothing().given(useCase).handle(any());

            // when & then
            performDocument(request, status().isOk(), "success-refund", "success");
        }

        @Test
        @DisplayName("[success] Voided Purchase 알림을 정상 처리한다")
        void success_voidedPurchase() throws Exception {
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
            GooglePlayWebhookRequest request = createRequest(jsonData);

            willDoNothing().given(useCase).handle(any());

            // when & then
            performDocument(request, status().isOk(), "success-voided", "success");
        }

        @Test
        @DisplayName("[error] 결제 정보를 찾을 수 없으면 500 에러를 반환한다")
        void error_paymentNotFound() throws Exception {
            // given
            String jsonData = createSubscriptionNotificationJson(2, "unknown_token", "subscription");
            GooglePlayWebhookRequest request = createRequest(jsonData);

            willThrow(new CustomBusinessException(ErrorCode.Business_NOT_FOUND_PAYMENT))
                .given(useCase).handle(any());

            // when & then
            performErrorDocument(request, status().isInternalServerError(), "결제 정보 없음");
        }
    }

    private String createSubscriptionNotificationJson(int notificationType, String purchaseToken,
        String subscriptionId) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"packageName\": \"com.odcloud.app\",");
        json.append("\"eventTimeMillis\": \"1704067200000\",");
        json.append("\"subscriptionNotification\": {");
        json.append("\"notificationType\": ").append(notificationType).append(",");
        json.append("\"purchaseToken\": \"").append(purchaseToken).append("\"");
        if (subscriptionId != null) {
            json.append(",\"subscriptionId\": \"").append(subscriptionId).append("\"");
        }
        json.append("}}");
        return json.toString();
    }

    private GooglePlayWebhookRequest createRequest(String jsonData) {
        String encodedData = Base64.getEncoder()
            .encodeToString(jsonData.getBytes(StandardCharsets.UTF_8));
        GooglePlayWebhookRequest.Message message = new GooglePlayWebhookRequest.Message(
            encodedData, "msg_" + System.currentTimeMillis(), "2024-01-01T00:00:00Z");
        return new GooglePlayWebhookRequest(message, "projects/odcloud/subscriptions/play-webhook");
    }

    private void performDocument(
        GooglePlayWebhookRequest request,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema
    ) throws Exception {
        mockMvc.perform(post("/webhook/googleplay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Webhook")
                    .summary("Google Play Webhook API")
                    .description(
                        "Google Play에서 발생하는 구독 관련 이벤트(갱신, 환불 등)를 처리하는 웹훅 API입니다. "
                            + "Google Cloud Pub/Sub을 통해 전달되는 메시지를 수신합니다.")
                    .requestFields(
                        fieldWithPath("message").type(JsonFieldType.OBJECT)
                            .description("Pub/Sub 메시지 객체"),
                        fieldWithPath("message.data").type(JsonFieldType.STRING)
                            .description("Base64 인코딩된 알림 데이터"),
                        fieldWithPath("message.messageId").type(JsonFieldType.STRING)
                            .description("메시지 고유 ID"),
                        fieldWithPath("message.publishTime").type(JsonFieldType.STRING)
                            .description("메시지 발행 시간"),
                        fieldWithPath("subscription").type(JsonFieldType.STRING)
                            .description("Pub/Sub 구독 경로")
                    )
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        GooglePlayWebhookRequest request,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        FieldDescriptor[] errorResponseFields = new FieldDescriptor[]{
            fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                .description("상태 코드"),
            fieldWithPath("message").type(JsonFieldType.STRING)
                .description("상태 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT)
                .description("응답 데이터"),
            fieldWithPath("data.errorCode").type(JsonFieldType.NUMBER)
                .description("에러 코드"),
            fieldWithPath("data.errorMessage").type(JsonFieldType.STRING)
                .description("에러 메시지")
        };

        mockMvc.perform(post("/webhook/googleplay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, identifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Webhook")
                    .summary("Google Play Webhook API - Error")
                    .description(identifier)
                    .requestFields(
                        fieldWithPath("message").type(JsonFieldType.OBJECT)
                            .description("Pub/Sub 메시지 객체"),
                        fieldWithPath("message.data").type(JsonFieldType.STRING)
                            .description("Base64 인코딩된 알림 데이터"),
                        fieldWithPath("message.messageId").type(JsonFieldType.STRING)
                            .description("메시지 고유 ID"),
                        fieldWithPath("message.publishTime").type(JsonFieldType.STRING)
                            .description("메시지 발행 시간"),
                        fieldWithPath("subscription").type(JsonFieldType.STRING)
                            .description("Pub/Sub 구독 경로")
                    )
                    .responseFields(errorResponseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] error"))
                    .build()
                )
            ));
    }
}
