package com.odcloud.adapter.in.controller.auth.callback_google_oauth2;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
import com.odcloud.application.auth.port.in.CallbackGoogleOAuth2UseCase;
import com.odcloud.application.auth.service.callback_google_oauth2.CallbackGoogleOAuth2ServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

class CallbackGoogleOAuth2DocsTest extends RestDocsSupport {

    private final CallbackGoogleOAuth2UseCase useCase = mock(CallbackGoogleOAuth2UseCase.class);
    private final String apiName = "구글 OAuth2 콜백 API";

    @Override
    protected Object initController() {
        return new CallbackGoogleOAuth2Controller(useCase);
    }

    @Nested
    @DisplayName("[callback] 구글 OAuth2 콜백을 처리하는 API")
    class Describe_callback {

        @Test
        @DisplayName("[success] 유효한 인증 코드로 프론트엔드로 리다이렉트한다")
        void success() throws Exception {
            // given
            String code = "valid-google-auth-code";
            CallbackGoogleOAuth2ServiceResponse serviceResponse = new CallbackGoogleOAuth2ServiceResponse(
                "Bearer google-access-token-123",
                "http://localhost:3000/auth/callback?googleAccessToken=Bearer+google-access-token-123"
            );
            given(useCase.callback(code)).willReturn(serviceResponse);

            // when & then
            mockMvc.perform(get("/auth/google")
                    .param("code", code))
                .andDo(print())
                .andExpect(status().isFound())
                .andDo(document(String.format("[%s] success", apiName),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("Auth")
                        .summary("구글 OAuth2 콜백 API")
                        .description("구글 OAuth2 콜백을 처리합니다. 프론트엔드로 302 리다이렉트하며 googleAccessToken을 쿼리 파라미터로 전달합니다.")
                        .queryParameters(
                            parameterWithName("code")
                                .description("구글 OAuth2 인증 코드 (필수)")
                        )
                        .requestSchema(Schema.schema("[request] " + apiName))
                        .responseSchema(Schema.schema("[response] success"))
                        .build()
                    )
                ));
        }

        @Test
        @DisplayName("[error] 인증 코드가 null인 경우 400 에러를 반환한다")
        void error_codeIsNull() throws Exception {
            mockMvc.perform(get("/auth/google"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document(String.format("[%s] 구글 인증코드 미입력", apiName),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("Auth")
                        .summary("구글 OAuth2 콜백 API")
                        .description("구글 OAuth2 콜백을 처리하는 API 입니다.")
                        .queryParameters(
                            parameterWithName("code")
                                .description("구글 OAuth2 인증 코드 (필수)")
                                .optional()
                        )
                        .responseFields(
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
                        )
                        .requestSchema(Schema.schema("[request] " + apiName))
                        .responseSchema(Schema.schema("[response] error"))
                        .build()
                    )
                ));
        }
    }
}
