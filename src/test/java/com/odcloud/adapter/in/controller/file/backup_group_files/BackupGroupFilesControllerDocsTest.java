package com.odcloud.adapter.in.controller.file.backup_group_files;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
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
import com.odcloud.application.file.port.in.BackupGroupFilesUseCase;
import com.odcloud.application.file.service.backup_group_files.BackupGroupFilesResponse;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

class BackupGroupFilesControllerDocsTest extends RestDocsSupport {

    private final BackupGroupFilesUseCase useCase = mock(BackupGroupFilesUseCase.class);
    private final String apiName = "그룹 파일 Google Drive 백업 API";

    @Override
    protected Object initController() {
        return new BackupGroupFilesController(useCase);
    }

    @Nested
    @DisplayName("[backup] 그룹 파일을 Google Drive에 백업하는 API")
    class Describe_backup {

        @Test
        @DisplayName("[success] 모든 그룹이 정상 백업되면 successCount를 응답한다")
        void success_allGroupsBackedUp() throws Exception {
            given(useCase.backup()).willReturn(BackupGroupFilesResponse.builder()
                .totalGroups(3)
                .successCount(2)
                .failCount(0)
                .skipCount(1)
                .build());

            mockMvc.perform(post("/files/backup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("[" + apiName + "] success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary(apiName)
                        .description(
                            "백업 활성화(backupYn=Y)된 모든 그룹의 미백업 파일 이력을 Google Drive에 증분 백업합니다. "
                                + "그룹 단위로 격리 처리되어 일부 그룹 실패 시에도 나머지 그룹은 계속 처리됩니다.")
                        .responseFields(backupResponseFields())
                        .responseSchema(Schema.schema("[response] " + apiName))
                        .build()
                    )
                ));
        }

        @Test
        @DisplayName("[success] 백업 대상 그룹이 없으면 모든 카운트가 0으로 응답한다")
        void success_noGroupsToBackup() throws Exception {
            given(useCase.backup()).willReturn(BackupGroupFilesResponse.builder()
                .totalGroups(0)
                .successCount(0)
                .failCount(0)
                .skipCount(0)
                .build());

            mockMvc.perform(post("/files/backup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("[" + apiName + "] success - no groups",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary(apiName + " - 백업 대상 없음")
                        .description("백업 활성화된 그룹이 없는 경우 모든 카운트가 0으로 응답됩니다.")
                        .responseFields(backupResponseFields())
                        .responseSchema(Schema.schema("[response] " + apiName))
                        .build()
                    )
                ));
        }

        @Test
        @DisplayName("[success] 일부 그룹에서 failCount가 발생해도 200을 응답한다")
        void success_partialFailure() throws Exception {
            given(useCase.backup()).willReturn(BackupGroupFilesResponse.builder()
                .totalGroups(5)
                .successCount(3)
                .failCount(2)
                .skipCount(0)
                .build());

            mockMvc.perform(post("/files/backup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("[" + apiName + "] success - partial failure",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary(apiName + " - 부분 실패")
                        .description(
                            "일부 그룹/이력에서 Drive API 실패가 발생해도 HTTP 200으로 응답합니다. "
                                + "failCount를 통해 실패 여부를 확인할 수 있습니다.")
                        .responseFields(backupResponseFields())
                        .responseSchema(Schema.schema("[response] " + apiName))
                        .build()
                    )
                ));
        }

        @Test
        @DisplayName("[error] UseCase에서 예상치 못한 예외 발생 시 500을 응답한다")
        void error_unexpectedExceptionReturns500() throws Exception {
            // 이 테스트는 실제 BackupGroupFilesService가 내부적으로 모든 예외를 catch하여 카운트로 흡수하므로
            // UseCase.backup()이 예외를 전파하는 시나리오는 프로덕션에서 발생하지 않는다.
            // 목적: UseCase 레벨에서 예외가 전파될 경우 ExceptionAdvice가 500을 올바르게 반환하는지
            // 컨트롤러-ExceptionAdvice 연동을 검증한다.
            willThrow(new CustomBusinessException(ErrorCode.Business_GOOGLE_DRIVE_ENSURE_FOLDER_ERROR))
                .given(useCase).backup();

            mockMvc.perform(post("/files/backup"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andDo(document("[" + apiName + "] error - unexpected exception",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary(apiName + " - 서버 오류")
                        .description(
                            "UseCase 레벨에서 처리되지 않은 예외 발생 시 500을 반환합니다. "
                                + "정상적으로는 그룹 단위 격리 처리로 500이 발생하지 않습니다.")
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

    private org.springframework.restdocs.payload.FieldDescriptor[] backupResponseFields() {
        return new org.springframework.restdocs.payload.FieldDescriptor[]{
            fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                .description("HTTP 상태 코드"),
            fieldWithPath("message").type(JsonFieldType.STRING)
                .description("응답 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT)
                .description("응답 데이터"),
            fieldWithPath("data.totalGroups").type(JsonFieldType.NUMBER)
                .description("백업 활성화된 전체 그룹 수"),
            fieldWithPath("data.successCount").type(JsonFieldType.NUMBER)
                .description("백업 완료된 그룹 수"),
            fieldWithPath("data.failCount").type(JsonFieldType.NUMBER)
                .description("백업 실패가 발생한 그룹 수"),
            fieldWithPath("data.skipCount").type(JsonFieldType.NUMBER)
                .description("미백업 이력이 없어 건너뛴 그룹 수")
        };
    }
}
