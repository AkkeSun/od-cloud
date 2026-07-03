package com.odcloud.adapter.in.controller.subscription.register_subscription;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import com.odcloud.application.subscription.port.in.RegisterSubscriptionUseCase;
import com.odcloud.application.subscription.service.register_subscription.RegisterSubscriptionResponse;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class RegisterSubscriptionControllerDocsTest extends RestDocsSupport {

    private final RegisterSubscriptionUseCase useCase = mock(RegisterSubscriptionUseCase.class);
    private final String apiName = "구독 등록 API";

    @Override
    protected Object initController() {
        return new RegisterSubscriptionController(useCase);
    }

    @Nested
    @DisplayName("[register] 구독을 등록하는 API")
    class Describe_register {

        @Test
        @DisplayName("[success] 유효한 정보로 구독을 등록한다")
        void success() throws Exception {
            // given
            RegisterSubscriptionRequest request = RegisterSubscriptionRequest.builder()
                .groupId(1L)
                .productId(100L)
                .billingKey("billing-key-123")
                .build();

            given(useCase.register(any()))
                .willReturn(RegisterSubscriptionResponse.of(10L, 20L));

            // when & then
            performDocument(request, "Bearer test", status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("등록 성공 여부"),
                fieldWithPath("data.subscriptionId").type(JsonFieldType.NUMBER)
                    .description("등록된 구독 ID"),
                fieldWithPath("data.paymentId").type(JsonFieldType.NUMBER)
                    .description("등록된 결제 ID")
            );
        }

        @Test
        @DisplayName("[error] 그룹 ID 를 입력하지 않은 경우 400 에러를 반환한다")
        void error_groupIdIsNull() throws Exception {
            // given
            RegisterSubscriptionRequest request = RegisterSubscriptionRequest.builder()
                .productId(100L)
                .billingKey("billing-key-123")
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(), "그룹 ID 미입력");
        }

        @Test
        @DisplayName("[error] 빌링키를 입력하지 않은 경우 400 에러를 반환한다")
        void error_billingKeyIsBlank() throws Exception {
            // given
            RegisterSubscriptionRequest request = RegisterSubscriptionRequest.builder()
                .groupId(1L)
                .productId(100L)
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(), "빌링키 미입력");
        }

        @Test
        @DisplayName("[error] 이미 활성화된 동일 구독이 존재하면 에러를 반환한다")
        void error_alreadyExistsSubscription() throws Exception {
            // given
            RegisterSubscriptionRequest request = RegisterSubscriptionRequest.builder()
                .groupId(1L)
                .productId(100L)
                .billingKey("billing-key-123")
                .build();

            given(useCase.register(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_ALREADY_EXISTS_SUBSCRIPTION));

            // when & then
            performErrorDocument(request, "Bearer test", status().isInternalServerError(),
                "이미 활성화된 구독 존재");
        }
    }

    private void performDocument(
        RegisterSubscriptionRequest request,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        JsonFieldType groupIdType = request.groupId() == null ?
            JsonFieldType.NULL : JsonFieldType.NUMBER;
        JsonFieldType productIdType = request.productId() == null ?
            JsonFieldType.NULL : JsonFieldType.NUMBER;
        JsonFieldType billingKeyType = request.billingKey() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;

        mockMvc.perform(post("/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Subscription")
                    .summary("구독 등록 API")
                    .description("빌링키로 상품을 결제하고 그룹 구독을 등록하는 API 입니다.<br><br>"
                        + "- userId, groupId 유효성을 검증합니다.<br>"
                        + "- 동일한 groupId, productId 의 활성화된 구독이 있으면 등록을 거절합니다.<br>"
                        + "- PG사를 통해 billingKey 유효성을 검증합니다.<br>"
                        + "- Subscription 과 Payment 정보를 등록합니다.")
                    .requestFields(
                        fieldWithPath("groupId").type(groupIdType)
                            .description("구독할 그룹 ID"),
                        fieldWithPath("productId").type(productIdType)
                            .description("구독할 상품 ID"),
                        fieldWithPath("billingKey").type(billingKeyType)
                            .description("PG사 발급 빌링키")
                    )
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        RegisterSubscriptionRequest request,
        String authorization,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(request, authorization, status, identifier, "error",
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
        );
    }
}
