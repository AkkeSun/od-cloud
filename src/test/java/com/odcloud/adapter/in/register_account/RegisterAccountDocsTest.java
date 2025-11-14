package com.odcloud.adapter.in.register_account;

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
import com.odcloud.application.port.in.RegisterAccountUseCase;
import com.odcloud.application.service.register_account.RegisterAccountServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class RegisterAccountDocsTest extends RestDocsSupport {

    private final RegisterAccountUseCase useCase = mock(RegisterAccountUseCase.class);
    private final String apiName = "계정 등록 API";

    @Override
    protected Object initController() {
        return new RegisterAccountController(useCase);
    }

    @Nested
    @DisplayName("[registerAccount] 계정을 등록하는 API")
    class Describe_registerAccount {

        @Test
        @DisplayName("[success] 유효한 정보로 계정을 등록한다")
        void success() throws Exception {
            // given
            String googleAuthorization = "Bearer google-token-123";
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("group-abc123")
                .build();

            RegisterAccountServiceResponse serviceResponse =
                RegisterAccountServiceResponse.ofSuccess();

            given(useCase.register(any())).willReturn(serviceResponse);

            // when & then
            performDocument(googleAuthorization, request, status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("등록 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 이름이 빈 문자열인 경우 400 에러를 반환한다")
        void error_nameIsBlank() throws Exception {
            // given
            String googleAuthorization = "Bearer google-token-123";
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name(null)
                .groupId("group-abc123")
                .build();

            // when & then
            performErrorDocument(googleAuthorization, request, status().isBadRequest(), "이름 미입력");
        }

        @Test
        @DisplayName("[error] 그룹 아이디가 빈 문자열인 경우 400 에러를 반환한다")
        void error_groupIdIsEmpty() throws Exception {
            // given
            String googleAuthorization = "Bearer google-token-123";
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId(null)
                .build();

            // when & then
            performErrorDocument(googleAuthorization, request, status().isBadRequest(),
                "그룹 아이디 미입력");
        }

        @Test
        @DisplayName("[error] 유효하지 않은 구글 토큰인 경우 401 에러를 반환한다")
        void error_invalidGoogleToken() throws Exception {
            // given
            String googleAuthorization = "Bearer invalid-token";
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("group-abc123")
                .build();

            given(useCase.register(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomAuthenticationException(
                    com.odcloud.infrastructure.exception.ErrorCode.INVALID_GOOGLE_TOKEN));

            // when & then
            performErrorDocument(googleAuthorization, request, status().isUnauthorized(),
                "유효하지 않은 구글 토큰 입력");
        }

        @Test
        @DisplayName("[error] 이미 등록된 사용자인 경우 500 에러를 반환한다")
        void error_savedUser() throws Exception {
            // given
            String googleAuthorization = "Bearer google-token-123";
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("group-abc123")
                .build();

            given(useCase.register(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_USER));

            // when & then
            performErrorDocument(googleAuthorization, request, status().isInternalServerError(),
                "이미 등록된 사용자 정보 입력");
        }

        @Test
        @DisplayName("[error] 등록되지 않은 그룹인 경우 500 에러를 반환한다")
        void error_groupDoesNotExist() throws Exception {
            // given
            String googleAuthorization = "Bearer google-token-123";
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("non-existent-group")
                .build();

            given(useCase.register(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_GROUP));

            // when & then
            performErrorDocument(googleAuthorization, request, status().isInternalServerError(),
                "등록되지 않은 그룹명 입력");
        }

        @Test
        @DisplayName("[error] 구글 사용자 정보 조회 중 오류가 발생한 경우 500 에러를 반환한다")
        void error_googleUserInfoError() throws Exception {
            // given
            String googleAuthorization = "Bearer google-token-123";
            RegisterAccountRequest request = RegisterAccountRequest.builder()
                .name("홍길동")
                .groupId("group-abc123")
                .build();

            given(useCase.register(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_GOOGLE_USER_INFO_ERROR));

            // when & then
            performErrorDocument(googleAuthorization, request, status().isInternalServerError(),
                "구글 사용자 정보 조회 오류");
        }
    }

    private void performDocument(
        String googleAuthorization,
        RegisterAccountRequest request,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        JsonFieldType nameType = request.name() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType groupIdType = request.groupId() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;

        mockMvc.perform(post("/accounts")
                .header("googleAuthorization", googleAuthorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Account")
                    .summary("계정 등록 API")
                    .description("구글 인증 토큰과 사용자 정보로 계정을 등록합니다.")
                    .requestHeaders(
                        headerWithName("googleAuthorization")
                            .description("구글 인증 토큰")
                    )
                    .requestFields(
                        fieldWithPath("name").type(nameType)
                            .description("사용자 이름"),
                        fieldWithPath("groupId").type(groupIdType)
                            .description("그룹 ID")
                    )
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        String googleAuthorization,
        RegisterAccountRequest request,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(googleAuthorization, request, status, identifier, "error",
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
