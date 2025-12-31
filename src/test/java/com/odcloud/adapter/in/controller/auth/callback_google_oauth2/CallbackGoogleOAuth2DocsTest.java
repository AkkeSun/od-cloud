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
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

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
        @DisplayName("[success] 유효한 인증 코드로 구글 액세스 토큰을 발급받는다")
        void success() throws Exception {
            // given
            String code = "valid-google-auth-code";
            CallbackGoogleOAuth2ServiceResponse serviceResponse =
                new CallbackGoogleOAuth2ServiceResponse("google-access-token-123");

            given(useCase.callback(code)).willReturn(serviceResponse);

            // when & then
            performDocument(
                code,
                status().isOk(),
                "success",
                "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.googleAccessToken").type(JsonFieldType.STRING)
                    .description("구글 액세스 토큰")
            );
        }

        @Test
        @DisplayName("[error] 인증 코드가 null인 경우 400 에러를 반환한다")
        void error_codeIsNull() throws Exception {
            // when & then
            performErrorDocument(
                null,
                status().isBadRequest(),
                "구글 인증코드 미입력"
            );
        }
    }

    private void performDocument(
        String code,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(get("/auth/google")
                .param("code", code))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Auth")
                    .summary("구글 OAuth2 콜백 API")
                    .description("구글 OAuth2 콜백을 처리하는 API 입니다.")
                    .queryParameters(
                        parameterWithName("code")
                            .description("구글 OAuth2 인증 코드 (필수)")
                    )
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(String code,
        ResultMatcher status, String identifier) throws Exception {
        performDocument(code, status, identifier, "error",
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
