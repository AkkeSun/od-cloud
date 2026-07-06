package com.odcloud.adapter.in.controller.subscription.modify_subscription_plan;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.subscription.port.in.ModifySubscriptionPlanUseCase;
import com.odcloud.application.subscription.service.modify_subscription_plan.ModifySubscriptionPlanResponse;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class ModifySubscriptionPlanControllerDocsTest extends RestDocsSupport {

    private final ModifySubscriptionPlanUseCase useCase = mock(ModifySubscriptionPlanUseCase.class);
    private final String apiName = "구독 플랜 변경 API";

    @Override
    protected Object initController() {
        return new ModifySubscriptionPlanController(useCase);
    }

    @Nested
    @DisplayName("[modify] 구독 플랜을 변경하는 API")
    class Describe_modify {

        @Test
        @DisplayName("[success] 신규 상품가가 더 비싸면 업그레이드 처리한다")
        void success_upgrade() throws Exception {
            // given
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .currentSubscriptionId(1L)
                .newProductId(200L)
                .build();

            given(useCase.modify(any())).willReturn(
                ModifySubscriptionPlanResponse.ofUpgrade(1L, 2L, 3L, BigDecimal.valueOf(15000)));

            // when & then
            performDocument(request, "Bearer test", status().isOk(), "success-upgrade", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("처리 성공 여부"),
                fieldWithPath("data.changeType").type(JsonFieldType.STRING)
                    .description("변경 유형(UPGRADE/DOWNGRADE)"),
                fieldWithPath("data.previousSubscriptionId").type(JsonFieldType.NUMBER)
                    .description("기존 구독 ID"),
                fieldWithPath("data.newSubscriptionId").type(JsonFieldType.NUMBER)
                    .description("신규로 생성된 구독 ID"),
                fieldWithPath("data.paymentId").type(JsonFieldType.NUMBER)
                    .description("업그레이드 시 결제 ID (다운그레이드 시 null)"),
                fieldWithPath("data.chargedAmount").type(JsonFieldType.NUMBER)
                    .description("업그레이드 시 결제된 차액 (다운그레이드 시 0)")
            );
        }

        @Test
        @DisplayName("[success] 신규 상품가가 더 저렴하면 다운그레이드 처리한다")
        void success_downgrade() throws Exception {
            // given
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .currentSubscriptionId(1L)
                .newProductId(50L)
                .build();

            given(useCase.modify(any())).willReturn(
                ModifySubscriptionPlanResponse.ofDowngrade(1L, 2L));

            // when & then
            performDocument(request, "Bearer test", status().isOk(), "success-downgrade", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("처리 성공 여부"),
                fieldWithPath("data.changeType").type(JsonFieldType.STRING)
                    .description("변경 유형(UPGRADE/DOWNGRADE)"),
                fieldWithPath("data.previousSubscriptionId").type(JsonFieldType.NUMBER)
                    .description("기존 구독 ID"),
                fieldWithPath("data.newSubscriptionId").type(JsonFieldType.NUMBER)
                    .description("신규로 생성된 구독 ID"),
                fieldWithPath("data.paymentId").type(JsonFieldType.NULL)
                    .description("업그레이드 시 결제 ID (다운그레이드 시 null)"),
                fieldWithPath("data.chargedAmount").type(JsonFieldType.NUMBER)
                    .description("업그레이드 시 결제된 차액 (다운그레이드 시 0)")
            );
        }

        @Test
        @DisplayName("[error] 구독 소유자가 아니면 403 에러를 반환한다")
        void error_accessDenied() throws Exception {
            // given
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .currentSubscriptionId(1L)
                .newProductId(200L)
                .build();

            given(useCase.modify(any())).willThrow(
                new CustomAuthorizationException(ErrorCode.ACCESS_DENIED));

            // when & then
            performErrorDocument(request, "Bearer test", status().isForbidden(), "구독 소유자가 아님");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 구독이면 에러를 반환한다")
        void error_notFoundSubscription() throws Exception {
            // given
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .currentSubscriptionId(999L)
                .newProductId(200L)
                .build();

            given(useCase.modify(any())).willThrow(
                new CustomBusinessException(ErrorCode.Business_NOT_FOUND_SUBSCRIPTION));

            // when & then
            performErrorDocument(request, "Bearer test", status().isInternalServerError(), "존재하지 않는 구독");
        }

        @Test
        @DisplayName("[error] 신규 상품이 존재하지 않으면 에러를 반환한다")
        void error_notFoundProduct() throws Exception {
            // given
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .currentSubscriptionId(1L)
                .newProductId(999L)
                .build();

            given(useCase.modify(any())).willThrow(
                new CustomBusinessException(ErrorCode.Business_NOT_FOUND_PRODUCT));

            // when & then
            performErrorDocument(request, "Bearer test", status().isInternalServerError(), "존재하지 않는 상품");
        }

        @Test
        @DisplayName("[error] 구독 상태가 ACTIVE가 아니면 에러를 반환한다")
        void error_invalidStatus() throws Exception {
            // given
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .currentSubscriptionId(1L)
                .newProductId(200L)
                .build();

            given(useCase.modify(any())).willThrow(
                new CustomBusinessException(ErrorCode.Business_INVALID_SUBSCRIPTION_STATUS_FOR_MODIFY));

            // when & then
            performErrorDocument(request, "Bearer test", status().isInternalServerError(),
                "ACTIVE 상태가 아닌 구독");
        }

        @Test
        @DisplayName("[error] 신규 상품가가 기존 상품가와 동일하면 에러를 반환한다")
        void error_invalidPlanChange() throws Exception {
            // given
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .currentSubscriptionId(1L)
                .newProductId(100L)
                .build();

            given(useCase.modify(any())).willThrow(
                new CustomBusinessException(ErrorCode.Business_INVALID_PLAN_CHANGE));

            // when & then
            performErrorDocument(request, "Bearer test", status().isInternalServerError(), "동일한 상품가");
        }

        @Test
        @DisplayName("[error] 신규 상품에 대해 그룹 내 이미 활성화된 구독이 있으면 에러를 반환한다")
        void error_alreadyExistsSubscription() throws Exception {
            // given
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .currentSubscriptionId(1L)
                .newProductId(200L)
                .build();

            given(useCase.modify(any())).willThrow(
                new CustomBusinessException(ErrorCode.Business_ALREADY_EXISTS_SUBSCRIPTION));

            // when & then
            performErrorDocument(request, "Bearer test", status().isInternalServerError(),
                "그룹 내 이미 활성화된 구독 존재");
        }

        @Test
        @DisplayName("[error] 업그레이드 차액 결제에 실패하면 에러를 반환한다")
        void error_paymentFailed() throws Exception {
            // given
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .currentSubscriptionId(1L)
                .newProductId(200L)
                .build();

            given(useCase.modify(any())).willThrow(
                new CustomBusinessException(ErrorCode.Business_PLAN_CHANGE_PAYMENT_FAILED));

            // when & then
            performErrorDocument(request, "Bearer test", status().isInternalServerError(), "차액 결제 실패");
        }

        @Test
        @DisplayName("[error] 현재 구독 ID를 입력하지 않은 경우 400 에러를 반환한다")
        void error_currentSubscriptionIdIsNull() throws Exception {
            // given
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .newProductId(200L)
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(), "현재 구독 ID 미입력");
        }

        @Test
        @DisplayName("[error] 신규 상품 ID를 입력하지 않은 경우 400 에러를 반환한다")
        void error_newProductIdIsNull() throws Exception {
            // given
            ModifySubscriptionPlanRequest request = ModifySubscriptionPlanRequest.builder()
                .currentSubscriptionId(1L)
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(), "신규 상품 ID 미입력");
        }
    }

    private void performDocument(
        ModifySubscriptionPlanRequest request,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        JsonFieldType currentSubscriptionIdType = request.currentSubscriptionId() == null ?
            JsonFieldType.NULL : JsonFieldType.NUMBER;
        JsonFieldType newProductIdType = request.newProductId() == null ?
            JsonFieldType.NULL : JsonFieldType.NUMBER;

        mockMvc.perform(patch("/subscriptions/plans")
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
                    .summary("구독 플랜 변경 API")
                    .description("그룹이 이미 보유한 활성 구독의 상품(플랜)을 다른 상품으로 변경하는 API 입니다.<br><br>"
                        + "- 신규 상품가가 더 비싸면 업그레이드(즉시 전환 + 잔여가치를 정산한 차액 결제)로 처리합니다.<br>"
                        + "- 신규 상품가가 더 저렴하면 다운그레이드(현재 구독은 해지 예약, 신규 구독은 예약 등록)로 처리합니다.<br>"
                        + "- 구독의 buyerId 와 로그인 사용자가 다르면 접근 권한 예외가 발생합니다.<br>"
                        + "- 구독 상태가 ACTIVE 가 아니면 예외가 발생합니다.<br>"
                        + "- 신규 상품가가 기존 상품가와 동일하면 예외가 발생합니다.<br>"
                        + "- 그룹 내에 신규 상품에 대한 활성 구독이 이미 존재하면 예외가 발생합니다.")
                    .requestFields(
                        fieldWithPath("currentSubscriptionId").type(currentSubscriptionIdType)
                            .description("변경 대상이 되는 현재 구독의 ID"),
                        fieldWithPath("newProductId").type(newProductIdType)
                            .description("변경하려는 신규 상품(플랜) ID")
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
        ModifySubscriptionPlanRequest request,
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
