package com.odcloud.adapter.in.controller.subscription.reactivate_subscription;

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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.subscription.port.in.ReactivateSubscriptionUseCase;
import com.odcloud.application.subscription.service.reactivate_subscription.ReactivateSubscriptionResponse;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class ReactivateSubscriptionControllerDocsTest extends RestDocsSupport {

    private final ReactivateSubscriptionUseCase useCase = mock(ReactivateSubscriptionUseCase.class);
    private final String apiName = "구독 재활성화 API";

    @Override
    protected Object initController() {
        return new ReactivateSubscriptionController(useCase);
    }

    @Nested
    @DisplayName("[reactivate] 구독을 재활성화하는 API")
    class Describe_reactivate {

        @Test
        @DisplayName("[success] 구독을 재활성화한다")
        void success() throws Exception {
            // given
            given(useCase.reactivate(any())).willReturn(ReactivateSubscriptionResponse.ofSuccess());

            // when & then
            performDocument(1L, "Bearer test", status().isOk(),
                "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("재활성화 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 존재하지 않는 구독이면 에러를 반환한다")
        void error_notFoundSubscription() throws Exception {
            // given
            given(useCase.reactivate(any())).willThrow(
                new CustomBusinessException(ErrorCode.Business_NOT_FOUND_SUBSCRIPTION));

            // when & then
            performErrorDocument(999L, "Bearer test",
                status().isInternalServerError(), "존재하지 않는 구독");
        }

        @Test
        @DisplayName("[error] 구독 소유자가 아니면 403 에러를 반환한다")
        void error_accessDenied() throws Exception {
            // given
            given(useCase.reactivate(any())).willThrow(
                new CustomAuthorizationException(ErrorCode.ACCESS_DENIED));

            // when & then
            performErrorDocument(1L, "Bearer test",
                status().isForbidden(), "구독 소유자가 아님");
        }

        @Test
        @DisplayName("[error] EXP_PENDING 상태가 아닌 구독이면 에러를 반환한다")
        void error_invalidStatus() throws Exception {
            // given
            given(useCase.reactivate(any())).willThrow(new CustomBusinessException(
                ErrorCode.Business_INVALID_SUBSCRIPTION_STATUS_FOR_REACTIVATE));

            // when & then
            performErrorDocument(1L, "Bearer test",
                status().isInternalServerError(), "EXP_PENDING 상태가 아닌 구독");
        }
    }

    private void performDocument(
        Long subscriptionId,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(patch("/subscriptions/{subscriptionId}/reactivation", subscriptionId)
                .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Subscription")
                    .summary("구독 재활성화 API")
                    .description("로그인 사용자가 취소 신청(EXP_PENDING)한 본인 구독을 다시 활성화(ACTIVE)하는 API 입니다.<br><br>"
                        + "- 구독 상태를 EXP_PENDING 에서 ACTIVE 로 전환합니다.<br>"
                        + "- 결제/만료일/다음 결제일은 변경되지 않습니다.<br>"
                        + "- 구독의 buyerId 와 로그인 사용자가 다르면 접근 권한 예외가 발생합니다.<br>"
                        + "- 구독 상태가 EXP_PENDING 이 아니면 예외가 발생합니다.")
                    .pathParameters(
                        parameterWithName("subscriptionId").description("재활성화할 구독 ID")
                    )
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .responseFields(responseFields)
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        Long subscriptionId,
        String authorization,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(subscriptionId, authorization, status, identifier, "error",
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
