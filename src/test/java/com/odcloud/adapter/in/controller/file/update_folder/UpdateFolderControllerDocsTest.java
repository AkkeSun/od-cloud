package com.odcloud.adapter.in.controller.file.update_folder;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
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
import com.odcloud.application.port.in.UpdateFolderUseCase;
import com.odcloud.application.service.update_folder.UpdateFolderServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class UpdateFolderControllerDocsTest extends RestDocsSupport {

    private final UpdateFolderUseCase useCase = mock(UpdateFolderUseCase.class);
    private final String apiName = "폴더 정보 수정 API";

    @Override
    protected Object initController() {
        return new UpdateFolderController(useCase);
    }

    @Nested
    @DisplayName("[updateFolder] 폴더 정보를 수정하는 API")
    class Describe_updateFolder {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .name("수정된 폴더명")
                .build();
            String authorization = "error token";
            given(useCase.updateFolder(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument(1L, request, authorization, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 폴더명만 수정한다")
        void success_updateNameOnly() throws Exception {
            // given
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .name("수정된 폴더명")
                .build();

            UpdateFolderServiceResponse serviceResponse = UpdateFolderServiceResponse.ofSuccess();
            given(useCase.updateFolder(any())).willReturn(serviceResponse);

            // when & then
            performDocument(1L, request, "Bearer test", status().isOk(), "success_폴더명만_수정",
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
        @DisplayName("[success] 상위 폴더를 변경한다")
        void success_updateParentFolder() throws Exception {
            // given
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .parentId(2L)
                .build();

            UpdateFolderServiceResponse serviceResponse = UpdateFolderServiceResponse.ofSuccess();
            given(useCase.updateFolder(any())).willReturn(serviceResponse);

            // when & then
            performDocument(1L, request, "Bearer test", status().isOk(),
                "success_상위폴더_변경", "success",
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
        @DisplayName("[success] 폴더명, 상위 폴더를 모두 수정한다")
        void success_updateAll() throws Exception {
            // given
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .name("수정된 폴더명")
                .parentId(2L)
                .build();

            UpdateFolderServiceResponse serviceResponse = UpdateFolderServiceResponse.ofSuccess();
            given(useCase.updateFolder(any())).willReturn(serviceResponse);

            // when & then
            performDocument(1L, request, "Bearer test", status().isOk(), "success", "success",
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
        @DisplayName("[error] 폴더 소유자가 아닌 경우 403 에러를 반환한다")
        void error_accessDenied() throws Exception {
            // given
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .name("수정된 폴더명")
                .build();

            given(useCase.updateFolder(any()))
                .willThrow(new CustomAuthorizationException(ErrorCode.ACCESS_DENIED));

            // when & then
            performErrorDocument(1L, request, "Bearer test", status().isForbidden(),
                "폴더 소유자 아님");
        }

        @Test
        @DisplayName("[error] 다른 그룹의 상위 폴더로 이동 시도 시 500 에러를 반환한다")
        void error_differentGroup() throws Exception {
            // given
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .parentId(99L)
                .build();

            given(useCase.updateFolder(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_FORBIDDEN_ACCESS));

            // when & then
            performErrorDocument(1L, request, "Bearer test", status().isInternalServerError(),
                "다른 그룹의 폴더로 이동 시도");
        }

        @Test
        @DisplayName("[error] 상위 폴더에 동일한 이름의 폴더가 존재하는 경우 500 에러를 반환한다")
        void error_duplicateFolderName() throws Exception {
            // given
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .name("기존 폴더명")
                .build();

            given(useCase.updateFolder(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_SAVED_FOLDER_NAME));

            // when & then
            performErrorDocument(1L, request, "Bearer test", status().isInternalServerError(),
                "폴더명 중복");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 폴더 수정 시도 시 500 에러를 반환한다")
        void error_folderNotFound() throws Exception {
            // given
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .name("수정된 폴더명")
                .build();

            given(useCase.updateFolder(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_DoesNotExists_FOLDER));

            // when & then
            performErrorDocument(999L, request, "Bearer test", status().isInternalServerError(),
                "존재하지 않는 폴더");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 상위 폴더로 이동 시도 시 500 에러를 반환한다")
        void error_parentFolderNotFound() throws Exception {
            // given
            UpdateFolderRequest request = UpdateFolderRequest.builder()
                .parentId(999L)
                .build();

            given(useCase.updateFolder(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_DoesNotExists_FOLDER));

            // when & then
            performErrorDocument(1L, request, "Bearer test", status().isInternalServerError(),
                "존재하지 않는 상위 폴더");
        }
    }

    private void performDocument(
        Long folderId,
        UpdateFolderRequest request,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        JsonFieldType nameType = request.name() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType parentIdType = request.parentId() == null ?
            JsonFieldType.NULL : JsonFieldType.NUMBER;

        mockMvc.perform(patch("/folders/{fileId}", folderId)
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
                    .summary("폴더 정보 수정 API")
                    .description("폴더의 이름, 상위 폴더를 수정하는 API 입니다")
                    .pathParameters(
                        parameterWithName("fileId").description("수정할 폴더 ID")
                    )
                    .requestFields(
                        fieldWithPath("name").type(nameType)
                            .description("폴더명 (선택)"),
                        fieldWithPath("parentId").type(parentIdType)
                            .description("상위 폴더 ID (선택)")
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
        Long folderId,
        UpdateFolderRequest request,
        String authorization,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(folderId, request, authorization, status, identifier, "error",
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
