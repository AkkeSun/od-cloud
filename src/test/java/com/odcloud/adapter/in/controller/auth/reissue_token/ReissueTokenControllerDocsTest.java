package com.odcloud.adapter.in.controller.auth.reissue_token;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.auth.port.in.ReissueTokenUseCase;
import com.odcloud.application.auth.service.reissue_token.ReissueTokenServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class ReissueTokenControllerDocsTest extends RestDocsSupport {

    private final ReissueTokenUseCase useCase = mock(ReissueTokenUseCase.class);
    private final String apiName = "토큰 재발급 API";

    @Override
    protected Object initController() {
        return new ReissueTokenController(useCase);
    }

    @Nested
    @DisplayName("[update] 리프레시 토큰으로 액세스 토큰과 리프레시 토큰을 재발급하는 API")
    class Describe_update {

        @Test
        @DisplayName("[success] 유효한 리프레시 토큰으로 토큰을 재발급받는다")
        void success() throws Exception {
            // given
            String refreshToken = "valid-refresh-token-123";
            ReissueTokenServiceResponse serviceResponse = ReissueTokenServiceResponse.builder()
                .accessToken("new-access-token-abc123")
                .refreshToken("new-refresh-token-xyz789")
                .build();

            given(useCase.reissueToken(refreshToken)).willReturn(serviceResponse);

            // when & then
            performDocument(refreshToken, status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                    .description("새로운 액세스 토큰"),
                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                    .description("새로운 리프레시 토큰")
            );
        }

        @Test
        @DisplayName("[error] 리프레시 토큰이 유효하지 않은 경우 401 에러를 반환한다")
        void error_invalidRefreshToken() throws Exception {
            // given
            String refreshToken = "invalid-refresh-token";
            given(useCase.reissueToken(refreshToken))
                .willThrow(new com.odcloud.infrastructure.exception.CustomAuthenticationException(
                    com.odcloud.infrastructure.exception.ErrorCode.INVALID_REFRESH_TOKEN));

            // when & then
            performErrorDocument(refreshToken, status().isUnauthorized(), "유효하지 않은 리프레시 토큰");
        }

        @Test
        @DisplayName("[error] Redis에 저장된 토큰과 불일치하는 경우 401 에러를 반환한다")
        void error_mismatchRefreshToken() throws Exception {
            // given
            String refreshToken = "mismatched-refresh-token";
            given(useCase.reissueToken(refreshToken))
                .willThrow(new com.odcloud.infrastructure.exception.CustomAuthenticationException(
                    com.odcloud.infrastructure.exception.ErrorCode.INVALID_REFRESH_TOKEN));

            // when & then
            performErrorDocument(refreshToken, status().isUnauthorized(), "Redis 토큰 불일치");
        }

        @Test
        @DisplayName("[error] 계정이 존재하지 않는 경우 500 에러를 반환한다")
        void error_accountNotFound() throws Exception {
            // given
            String refreshToken = "valid-refresh-token";
            given(useCase.reissueToken(refreshToken))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_ACCOUNT));

            // when & then
            performErrorDocument(refreshToken, status().isInternalServerError(), "계정 없음");
        }
    }

    private void performDocument(
        String refreshToken,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(put("/auth")
                .header("refreshToken", refreshToken))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Auth")
                    .summary("토큰 재발급 API")
                    .description("리프레시 토큰으로 새로운 액세스 토큰과 리프레시 토큰을 재발급합니다.")
                    .requestHeaders(
                        headerWithName("refreshToken")
                            .description("리프레시 토큰")
                    )
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(String refreshToken,
        ResultMatcher status, String identifier) throws Exception {
        performDocument(refreshToken, status, identifier, "error",
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
