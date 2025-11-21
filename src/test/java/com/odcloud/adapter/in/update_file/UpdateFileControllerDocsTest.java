package com.odcloud.adapter.in.update_file;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
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
import com.odcloud.application.port.in.UpdateFileUseCase;
import com.odcloud.application.service.update_file.UpdateFileServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class UpdateFileControllerDocsTest extends RestDocsSupport {

    private final UpdateFileUseCase useCase = mock(UpdateFileUseCase.class);
    private final String apiName = "파일 정보 수정 API";

    @Override
    protected Object initController() {
        return new UpdateFileController(useCase);
    }

    @Nested
    @DisplayName("[update] 파일 정보 수정 API")
    class Describe_update {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            Long fileId = 1L;
            String authorization = "error token";
            UpdateFileRequest request = UpdateFileRequest.builder()
                .fileName("new-name.txt")
                .build();

            given(useCase.update(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument(fileId, request, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력",
                authorization);
        }

        @Test
        @DisplayName("[success] 파일명과 폴더를 동시에 변경한다")
        void success_updateBoth() throws Exception {
            // given
            Long fileId = 1L;
            String authorization = "Bearer test";
            UpdateFileRequest request = UpdateFileRequest.builder()
                .fileName("new-name.txt")
                .folderId(2L)
                .build();

            UpdateFileServiceResponse serviceResponse = UpdateFileServiceResponse.ofSuccess();
            given(useCase.update(any())).willReturn(serviceResponse);

            // when & then
            performDocument(fileId, request, status().isOk(), "파일명과 폴더 동시 변경", "success",
                authorization,
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.result")
                    .type(JsonFieldType.BOOLEAN).description("수정 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 존재하지 않는 파일을 수정하려고 하면 500 코드와 에러 메시지를 응답한다.")
        void error_fileNotFound() throws Exception {
            // given
            Long fileId = 999L;
            String authorization = "Bearer test";
            UpdateFileRequest request = UpdateFileRequest.builder()
                .fileName("new-name.txt")
                .build();

            given(useCase.update(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_DoesNotExists_FILE));

            // when & then
            performErrorDocument(fileId, request, status().isInternalServerError(), "파일 없음",
                authorization);
        }

        @Test
        @DisplayName("[error] 동일한 파일명이 이미 존재하면 500 코드와 에러 메시지를 응답한다.")
        void error_duplicateFileName() throws Exception {
            // given
            Long fileId = 1L;
            String authorization = "Bearer test";
            UpdateFileRequest request = UpdateFileRequest.builder()
                .fileName("duplicate.txt")
                .build();

            given(useCase.update(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_SAVED_FILE_NAME));

            // when & then
            performErrorDocument(fileId, request, status().isInternalServerError(), "중복된 파일명",
                authorization);
        }

        @Test
        @DisplayName("[error] 접근 권한이 없는 폴더로 이동하려고 하면 500 코드와 에러 메시지를 응답한다.")
        void error_forbiddenAccess() throws Exception {
            // given
            Long fileId = 1L;
            String authorization = "Bearer test";
            UpdateFileRequest request = UpdateFileRequest.builder()
                .folderId(2L)
                .build();

            given(useCase.update(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_FORBIDDEN_ACCESS));

            // when & then
            performErrorDocument(fileId, request, status().isInternalServerError(), "접근 권한 없음",
                authorization);
        }

        @Test
        @DisplayName("[error] 존재하지 않는 대상 폴더를 지정하면 500 코드와 에러 메시지를 응답한다.")
        void error_targetFolderNotFound() throws Exception {
            // given
            Long fileId = 1L;
            String authorization = "Bearer test";
            UpdateFileRequest request = UpdateFileRequest.builder()
                .folderId(999L)
                .build();

            given(useCase.update(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_DoesNotExists_FOLDER));

            // when & then
            performErrorDocument(fileId, request, status().isInternalServerError(), "대상 폴더 없음",
                authorization);
        }
    }

    private void performDocument(
        Long fileId,
        UpdateFileRequest request,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        String authorization,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/files/{fileId}", fileId)
                    .header("Authorization", authorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary("파일 정보 수정 API")
                        .description("파일명 또는 폴더를 변경하는 API 입니다. <br>"
                            + "파일명과 폴더 아이디는 모두 옵션입니다. <br>"
                            + "- 파일명만 입력: 파일명만 변경 <br>"
                            + "- 폴더 아이디만 입력: 파일을 다른 폴더로 이동 <br>"
                            + "- 둘 다 입력: 파일명 변경 후 다른 폴더로 이동 <br>"
                            + "폴더 이동 시 해당 폴더에 대한 접근 권한을 확인합니다. <br>"
                            + "동일한 이름의 파일이 이미 존재하는 경우 오류가 발생합니다.")
                        .pathParameters(
                            parameterWithName("fileId").description("파일 ID")
                        )
                        .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                        .requestFields(
                            fieldWithPath("fileName").type(JsonFieldType.STRING)
                                .description("변경할 파일명 (옵션)").optional(),
                            fieldWithPath("folderId").type(JsonFieldType.NUMBER)
                                .description("이동할 폴더 ID (옵션)").optional()
                        )
                        .responseFields(responseFields)
                        .requestSchema(Schema.schema("[request] " + apiName))
                        .responseSchema(Schema.schema("[response] " + responseSchema))
                        .build()
                    )
                )
            );
    }

    private void performErrorDocument(
        Long fileId,
        UpdateFileRequest request,
        ResultMatcher status,
        String identifier,
        String authorization
    ) throws Exception {
        performDocument(fileId, request, status, identifier, "error", authorization,
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
