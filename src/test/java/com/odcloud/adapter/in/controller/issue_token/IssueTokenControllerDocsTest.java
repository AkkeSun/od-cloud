package com.odcloud.adapter.in.controller.issue_token;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
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
import com.odcloud.application.port.in.IssueTokenUseCase;
import com.odcloud.application.service.issue_token.IssueTokenServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class IssueTokenControllerDocsTest extends RestDocsSupport {

    private final IssueTokenUseCase useCase = mock(IssueTokenUseCase.class);
    private final String apiName = "토큰 발급 API";

    @Override
    protected Object initController() {
        return new IssueTokenController(useCase);
    }

    @Nested
    @DisplayName("[issue] 구글 인증 토큰으로 액세스 토큰과 리프레시 토큰을 발급하는 API")
    class Describe_issue {

        @Test
        @DisplayName("[success] 유효한 구글 인증 토큰으로 토큰을 발급받는다")
        void success() throws Exception {
            // given
            String googleAuthorization = "Bearer google-auth-token-123";
            IssueTokenServiceResponse serviceResponse = IssueTokenServiceResponse.builder()
                .accessToken("access-token-abc123")
                .refreshToken("refresh-token-xyz789")
                .build();

            given(useCase.issue(googleAuthorization)).willReturn(serviceResponse);

            // when & then
            performDocument(
                googleAuthorization,
                status().isOk(),
                "success",
                "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                    .description("액세스 토큰"),
                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                    .description("리프레시 토큰")
            );
        }

        @Test
        @DisplayName("[error] 유효하지 않은 구글 토큰인 경우 401 에러를 반환한다")
        void error_invalidGoogleToken() throws Exception {
            // given
            String googleAuthorization = "Bearer invalid-token";
            given(useCase.issue(googleAuthorization))
                .willThrow(new com.odcloud.infrastructure.exception.CustomAuthenticationException(
                    com.odcloud.infrastructure.exception.ErrorCode.INVALID_GOOGLE_TOKEN));

            // when & then
            performErrorDocument(
                googleAuthorization,
                status().isUnauthorized(),
                "유효하지 않은 구글 토큰"
            );
        }

        @Test
        @DisplayName("[error] 계정이 존재하지 않는 경우 500 에러를 반환한다")
        void error_accountNotFound() throws Exception {
            // given
            String googleAuthorization = "Bearer google-token-123";
            given(useCase.issue(googleAuthorization))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_ACCOUNT));

            // when & then
            performErrorDocument(
                googleAuthorization,
                status().isInternalServerError(),
                "계정 없음"
            );
        }

        @Test
        @DisplayName("[error] 승인된 그룹이 없는 사용자인 경우 500 에러를 반환한다")
        void error_emptyGroupAccount() throws Exception {
            // given
            String googleAuthorization = "Bearer google-token-123";
            given(useCase.issue(googleAuthorization))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_EMPTY_GROUP_ACCOUNT));

            // when & then
            performErrorDocument(
                googleAuthorization,
                status().isInternalServerError(),
                "승인된 그룹 없음"
            );
        }

        @Test
        @DisplayName("[error] 구글 사용자 정보 조회 중 오류가 발생한 경우 500 에러를 반환한다")
        void error_googleUserInfoError() throws Exception {
            // given
            String googleAuthorization = "Bearer google-token-123";
            given(useCase.issue(googleAuthorization))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_GOOGLE_USER_INFO_ERROR));

            // when & then
            performErrorDocument(
                googleAuthorization,
                status().isInternalServerError(),
                "구글 사용자 정보 조회 오류"
            );
        }
    }

    private void performDocument(
        String googleAuthorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(post("/auth")
                .header("googleAuthorization", googleAuthorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Auth")
                    .summary("토큰 발급 API")
                    .description("구글 인증 토큰으로 액세스 토큰과 리프레시 토큰을 발급합니다.")
                    .requestHeaders(
                        headerWithName("googleAuthorization")
                            .description("구글 인증 토큰 (필수)")
                    )
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(String googleAuthorization,
        ResultMatcher status, String identifier) throws Exception {
        performDocument(googleAuthorization, status, identifier, "error",
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
