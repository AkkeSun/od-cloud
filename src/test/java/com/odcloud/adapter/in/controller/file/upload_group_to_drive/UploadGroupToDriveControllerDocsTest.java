package com.odcloud.adapter.in.controller.file.upload_group_to_drive;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
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
import com.odcloud.application.file.port.in.UploadGroupToDriveUseCase;
import com.odcloud.application.file.service.upload_group_to_drive.UploadGroupToDriveResponse;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

class UploadGroupToDriveControllerDocsTest extends RestDocsSupport {

    private final UploadGroupToDriveUseCase useCase = mock(UploadGroupToDriveUseCase.class);
    private final String apiName = "그룹 파일 Google Drive 전체 업로드 API";

    @Override
    protected Object initController() {
        return new UploadGroupToDriveController(useCase);
    }

    @Nested
    @DisplayName("[upload] 그룹의 모든 파일을 Google Drive에 업로드하는 API")
    class Describe_upload {

        @Test
        @DisplayName("[success] 모든 파일이 정상 업로드되면 uploadedCount를 응답한다")
        void success_allFilesUploaded() throws Exception {
            given(useCase.upload(1L)).willReturn(UploadGroupToDriveResponse.builder()
                .totalFiles(5)
                .uploadedCount(5)
                .skippedCount(0)
                .failedCount(0)
                .build());

            mockMvc.perform(post("/groups/{groupId}/drive/upload", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("[" + apiName + "] success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary(apiName)
                        .description("그룹의 모든 파일을 Google Drive에 업로드합니다. 동일한 이름의 파일이 이미 존재하면 건너뜁니다.")
                        .pathParameters(
                            parameterWithName("groupId").description("업로드할 그룹 ID")
                        )
                        .responseFields(uploadResponseFields())
                        .responseSchema(Schema.schema("[response] " + apiName))
                        .build()
                    )
                ));
        }

        @Test
        @DisplayName("[success] 이미 존재하는 파일은 건너뛰고 skippedCount를 응답한다")
        void success_duplicateFilesSkipped() throws Exception {
            given(useCase.upload(1L)).willReturn(UploadGroupToDriveResponse.builder()
                .totalFiles(5)
                .uploadedCount(3)
                .skippedCount(2)
                .failedCount(0)
                .build());

            mockMvc.perform(post("/groups/{groupId}/drive/upload", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("[" + apiName + "] success - skipped",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary(apiName + " - 중복 파일 건너뜀")
                        .description("Drive에 동일한 이름의 파일이 이미 존재하는 경우 해당 파일은 건너뜁니다.")
                        .pathParameters(
                            parameterWithName("groupId").description("업로드할 그룹 ID")
                        )
                        .responseFields(uploadResponseFields())
                        .responseSchema(Schema.schema("[response] " + apiName))
                        .build()
                    )
                ));
        }

        @Test
        @DisplayName("[success] 업로드할 파일이 없으면 모든 카운트가 0으로 응답한다")
        void success_noFilesToUpload() throws Exception {
            given(useCase.upload(1L)).willReturn(UploadGroupToDriveResponse.builder()
                .totalFiles(0)
                .uploadedCount(0)
                .skippedCount(0)
                .failedCount(0)
                .build());

            mockMvc.perform(post("/groups/{groupId}/drive/upload", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("[" + apiName + "] success - no files",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary(apiName + " - 업로드 대상 없음")
                        .description("그룹에 파일이 없는 경우 모든 카운트가 0으로 응답됩니다.")
                        .pathParameters(
                            parameterWithName("groupId").description("업로드할 그룹 ID")
                        )
                        .responseFields(uploadResponseFields())
                        .responseSchema(Schema.schema("[response] " + apiName))
                        .build()
                    )
                ));
        }

        @Test
        @DisplayName("[error] 존재하지 않는 그룹 ID로 요청 시 500을 응답한다")
        void error_groupNotFound() throws Exception {
            willThrow(new CustomBusinessException(ErrorCode.Business_DoesNotExists_GROUP))
                .given(useCase).upload(999L);

            mockMvc.perform(post("/groups/{groupId}/drive/upload", 999L))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andDo(document("[" + apiName + "] error - group not found",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary(apiName + " - 그룹 없음")
                        .description("존재하지 않는 그룹 ID로 요청 시 오류를 반환합니다.")
                        .pathParameters(
                            parameterWithName("groupId").description("업로드할 그룹 ID")
                        )
                        .responseFields(
                            fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                                .description("HTTP 상태 코드"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT)
                                .description("에러 응답 데이터"),
                            fieldWithPath("data.errorCode").type(JsonFieldType.NUMBER)
                                .description("에러 코드"),
                            fieldWithPath("data.errorMessage").type(JsonFieldType.STRING)
                                .description("에러 메시지")
                        )
                        .responseSchema(Schema.schema("[response] error"))
                        .build()
                    )
                ));
        }
    }

    private org.springframework.restdocs.payload.FieldDescriptor[] uploadResponseFields() {
        return new org.springframework.restdocs.payload.FieldDescriptor[]{
            fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                .description("HTTP 상태 코드"),
            fieldWithPath("message").type(JsonFieldType.STRING)
                .description("응답 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT)
                .description("응답 데이터"),
            fieldWithPath("data.totalFiles").type(JsonFieldType.NUMBER)
                .description("그룹 전체 파일 수"),
            fieldWithPath("data.uploadedCount").type(JsonFieldType.NUMBER)
                .description("Drive에 업로드된 파일 수"),
            fieldWithPath("data.skippedCount").type(JsonFieldType.NUMBER)
                .description("동일한 이름의 파일이 이미 존재하여 건너뛴 파일 수"),
            fieldWithPath("data.failedCount").type(JsonFieldType.NUMBER)
                .description("업로드 실패한 파일 수")
        };
    }
}
