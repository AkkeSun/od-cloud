package com.odcloud.adapter.in.controller.file.register_folder;

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
import com.odcloud.application.file.port.in.RegisterFolderUseCase;
import com.odcloud.application.file.service.register_folder.RegisterFolderServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class RegisterFolderControllerDocsTest extends RestDocsSupport {

    private final RegisterFolderUseCase useCase = mock(RegisterFolderUseCase.class);
    private final String apiName = "폴더 등록 API";

    @Override
    protected Object initController() {
        return new RegisterFolderController(useCase);
    }

    @Nested
    @DisplayName("[createFolder] 폴더 생성 API")
    class Describe_createFolder {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error() throws Exception {
            // given
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(1L)
                .groupId(1L)
                .name("새 폴더")
                .build();
            String authorization = "error token";
            given(useCase.createFolder(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument(request, authorization, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 폴더가 정상적으로 생성된다.")
        void success() throws Exception {
            // given
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(1L)
                .groupId(1L)
                .name("새 폴더")
                .build();

            RegisterFolderServiceResponse serviceResponse =
                new RegisterFolderServiceResponse(true);

            given(useCase.createFolder(any())).willReturn(serviceResponse);

            // when & then
            performDocument(request, "Bearer token", status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("HTTP 상태코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("응답 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("폴더 생성 결과")
            );
        }

        @Test
        @DisplayName("[error] parentId 가 null 이면 400을 반환한다.")
        void error_parentIdIsNull() throws Exception {
            // given
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(null)
                .groupId(1L)
                .name("새 폴더")
                .build();

            // when & then
            performErrorDocument(request, "Bearer token", status().isBadRequest(), "부모 폴더 아이디 미입력");
        }

        @Test
        @DisplayName("[error] groupId 가 빈 문자열이면 400을 반환한다.")
        void error_groupIdIsBlank() throws Exception {
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(1L)
                .name("새 폴더")
                .build();

            performErrorDocument(request, "Bearer token", status().isBadRequest(), "그룹 아이디 미입력");
        }

        @Test
        @DisplayName("[error] name 이 빈 문자열이면 400을 반환한다.")
        void error_nameIsBlank() throws Exception {
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(1L)
                .groupId(1L)
                .build();

            performErrorDocument(request, "Bearer token", status().isBadRequest(), "이름 미입력");
        }

        @Test
        @DisplayName("[error] 부모 폴더가 존재하지 않으면 500을 반환한다.")
        void error_folderDoesNotExist() throws Exception {
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(999L)
                .groupId(1L)
                .name("새 폴더")
                .build();

            given(useCase.createFolder(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_FOLDER));

            performErrorDocument(request, "Bearer token", status().isInternalServerError(),
                "부모 폴더 없음");
        }

        @Test
        @DisplayName("[error] 동일한 이름의 폴더가 이미 존재하면 500을 반환한다.")
        void error_folderNameAlreadyExists() throws Exception {
            RegisterFolderRequest request = RegisterFolderRequest.builder()
                .parentId(1L)
                .groupId(1L)
                .name("중복 폴더")
                .build();

            given(useCase.createFolder(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_FOLDER_NAME));

            performErrorDocument(request, "Bearer token", status().isInternalServerError(),
                "중복된 폴더명");
        }
    }

    private void performDocument(
        RegisterFolderRequest request,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        JsonFieldType parentIdType = request.parentId() == null ?
            JsonFieldType.NULL : JsonFieldType.NUMBER;
        JsonFieldType groupIdType = request.groupId() == null ?
            JsonFieldType.NULL : JsonFieldType.NUMBER;
        JsonFieldType nameType = request.name() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;

        mockMvc.perform(post("/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("File")
                    .summary("폴더 생성 API")
                    .description("폴더를 생성하는 API 입니다.")
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .requestFields(
                        fieldWithPath("parentId").type(parentIdType)
                            .description("부모 폴더 ID"),
                        fieldWithPath("groupId").type(groupIdType)
                            .description("그룹 ID"),
                        fieldWithPath("name").type(nameType)
                            .description("폴더명")
                    )
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        RegisterFolderRequest request,
        String authorization,
        ResultMatcher status,
        String identifier
    ) throws Exception {

        performDocument(request, authorization, status, identifier, "error",
            fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                .description("HTTP 상태코드"),
            fieldWithPath("message").type(JsonFieldType.STRING)
                .description("응답 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT)
                .description("오류 데이터"),
            fieldWithPath("data.errorCode").type(JsonFieldType.NUMBER)
                .description("오류 코드"),
            fieldWithPath("data.errorMessage").type(JsonFieldType.STRING)
                .description("오류 메시지")
        );
    }
}
