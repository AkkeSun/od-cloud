package com.odcloud.adapter.in.controller.account.update_account;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.account.port.in.UpdateAccountUseCase;
import com.odcloud.application.account.service.update_account.UpdateAccountServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class UpdateAccountControllerDocsTest extends RestDocsSupport {

    private final UpdateAccountUseCase useCase = mock(UpdateAccountUseCase.class);
    private final String apiName = "계정 정보 수정 API";

    @Override
    protected Object initController() {
        return new UpdateAccountController(useCase);
    }

    @Nested
    @DisplayName("[updateAccount] 계정 정보를 수정하는 API")
    class Describe_updateAccount {

        @Test
        @DisplayName("[success] 유효한 정보로 계정을 수정한다")
        void success() throws Exception {
            // given
            String authorization = "Bearer valid-token";
            String nickname = "새닉네임";
            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "profile.png",
                "image/png",
                "image-content".getBytes()
            );

            UpdateAccountServiceResponse serviceResponse =
                UpdateAccountServiceResponse.ofSuccess();

            given(useCase.update(any())).willReturn(serviceResponse);

            // when & then
            performDocument(authorization, nickname, pictureFile, status().isOk(), "success",
                "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("수정 성공 여부")
            );
        }

        @Test
        @DisplayName("[success] 닉네임만 수정한다")
        void success_nicknameOnly() throws Exception {
            // given
            String authorization = "Bearer valid-token";
            String nickname = "새닉네임";

            UpdateAccountServiceResponse serviceResponse =
                UpdateAccountServiceResponse.ofSuccess();

            given(useCase.update(any())).willReturn(serviceResponse);

            // when & then
            performDocument(authorization, nickname, null, status().isOk(), "닉네임만 수정", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("수정 성공 여부")
            );
        }

        @Test
        @DisplayName("[success] 프로필 사진만 수정한다")
        void success_pictureOnly() throws Exception {
            // given
            String authorization = "Bearer valid-token";
            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "profile.png",
                "image/png",
                "image-content".getBytes()
            );

            UpdateAccountServiceResponse serviceResponse =
                UpdateAccountServiceResponse.ofSuccess();

            given(useCase.update(any())).willReturn(serviceResponse);

            // when & then
            performDocument(authorization, null, pictureFile, status().isOk(), "프로필 사진만 수정",
                "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("수정 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 유효하지 않은 토큰인 경우 401 에러를 반환한다")
        void error_invalidToken() throws Exception {
            // given
            String authorization = "Bearer invalid-token";
            String nickname = "새닉네임";

            given(useCase.update(any()))
                .willThrow(new CustomAuthenticationException(
                    ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when & then
            performErrorDocument(authorization, nickname, null, status().isUnauthorized(),
                "유효하지 않은 토큰 입력");
        }

        @Test
        @DisplayName("[error] 프로필 사진 형식이 올바르지 않은 경우 400 에러를 반환한다")
        void error_invalidPictureType() throws Exception {
            // given
            String authorization = "Bearer valid-token";
            MockMultipartFile pictureFile = new MockMultipartFile(
                "pictureFile",
                "profile.txt",
                "text/plain",
                "text-content".getBytes()
            );

            // when & then
            performErrorDocument(authorization, null, pictureFile, status().isBadRequest(),
                "유효하지 않은 파일 형식 입력");
        }

        @Test
        @DisplayName("[error] 계정을 찾을 수 없는 경우 500 에러를 반환한다")
        void error_accountNotFound() throws Exception {
            // given
            String authorization = "Bearer valid-token";
            String nickname = "새닉네임";

            given(useCase.update(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_ACCOUNT));

            // when & then
            performErrorDocument(authorization, nickname, null, status().isInternalServerError(),
                "계정 정보 없음");
        }
    }

    private void performDocument(
        String authorization,
        String nickname,
        MockMultipartFile pictureFile,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        var requestBuilder = RestDocumentationRequestBuilders
            .multipart("/accounts");

        if (pictureFile != null) {
            requestBuilder.file(pictureFile);
        }

        requestBuilder
            .with(request -> {
                request.setMethod("PATCH");
                return request;
            })
            .header("Authorization", authorization);

        if (nickname != null) {
            requestBuilder.param("nickname", nickname);
        }

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Account")
                    .summary("계정 정보 수정 API")
                    .description("사용자의 닉네임과 프로필 사진을 수정합니다. <br>"
                        + "RestDocs API 문서 작성 모듈 특성상 Multipart 입력 파라미터 정보 기록에 한계가 있어 아래 목록을 참고하시어 요청 바랍니다. <br><br>"
                        + "[입력받는 멀티파트 파라미터 목록]<br>"
                        + "- nickname : 수정할 닉네임 (선택) <br>"
                        + "- pictureFile : 수정할 프로필 사진 파일 (선택, PNG/JPG/JPEG 형식만 가능) <br>"
                        + "테스트시 우측 자물쇠를 클릭하여 유효한 인증 토큰을 입력해야 정상 테스트가 가능합니다. <br>"
                        + "(요청 헤더에 인증 토큰을 입력하여 테스트하지 않습니다)")
                    .requestHeaders(
                        headerWithName("Authorization")
                            .description("인증 토큰")
                    )
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        String authorization,
        String nickname,
        MockMultipartFile pictureFile,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(authorization, nickname, pictureFile, status, identifier, "error",
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
