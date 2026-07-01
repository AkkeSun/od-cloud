package com.odcloud.adapter.in.controller.file.download_file;

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
import com.odcloud.application.file.port.in.DownloadFileUseCase;
import com.odcloud.application.file.service.download_file.DownloadFileResponse;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class DownloadFileControllerDocsTest extends RestDocsSupport {

    private final DownloadFileUseCase useCase = mock(DownloadFileUseCase.class);
    private final String apiName = "단일 파일 다운로드 API";

    @Override
    protected Object initController() {
        return new DownloadFileController(useCase);
    }

    @Nested
    @DisplayName("[downloadFile] 단일 파일 다운로드 API")
    class Describe_downloadFile {

        @Test
        @DisplayName("[success] 접근 권한 검증 후 파일 URL을 응답한다")
        void success() throws Exception {
            // given
            Long fileId = 1L;
            String authorization = "Bearer test";

            DownloadFileResponse response = DownloadFileResponse.builder()
                .fileName("test.txt")
                .fileUrl("https://moimism.odlab.kr/test-group/folder1/test.txt")
                .build();

            given(useCase.downloadFile(any())).willReturn(response);

            // when & then
            performDocument(fileId, status().isOk(), "success", authorization);
        }

        @Test
        @DisplayName("[error] 존재하지 않는 파일 다운로드 시 500 코드와 에러 메시지를 응답한다")
        void error_fileNotFound() throws Exception {
            // given
            Long fileId = 999L;
            String authorization = "Bearer test";

            given(useCase.downloadFile(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_DoesNotExists_FILE));

            // when & then
            performErrorDocument(fileId, status().isInternalServerError(), "파일 조회 실패",
                authorization);
        }

        @Test
        @DisplayName("[error] 접근 권한이 없는 파일 다운로드 시 에러 메시지를 응답한다")
        void error_accessDenied() throws Exception {
            // given
            Long fileId = 1L;
            String authorization = "Bearer test";

            given(useCase.downloadFile(any()))
                .willThrow(new CustomAuthorizationException(ErrorCode.ACCESS_DENIED));

            // when & then
            performErrorDocument(fileId, status().isForbidden(), "접근 권한 없음", authorization);
        }
    }

    private void performDocument(
        Long fileId,
        ResultMatcher status,
        String docIdentifier,
        String authorization
    ) throws Exception {
        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/files/{fileId}/download", fileId)
                    .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary("파일 다운로드 API")
                        .description("파일에 대한 접근 권한을 검증하고, 클라이언트가 직접 다운로드할 수 있는 파일 URL을 응답하는 API 입니다. <br>"
                            + "테스트시 우측 자물쇠를 클릭하여 유효한 인증 토큰을 입력해야 정상 테스트가 가능합니다.")
                        .pathParameters(
                            parameterWithName("fileId").description("다운로드할 파일 ID")
                        )
                        .requestHeaders(
                            headerWithName("Authorization").description("인증 토큰")
                        )
                        .responseFields(
                            fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                                .description("상태 코드"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("상태 메시지"),
                            fieldWithPath("data").type(JsonFieldType.OBJECT)
                                .description("응답 데이터"),
                            fieldWithPath("data.fileName").type(JsonFieldType.STRING)
                                .description("파일명"),
                            fieldWithPath("data.fileUrl").type(JsonFieldType.STRING)
                                .description("파일 다운로드 URL")
                        )
                        .requestSchema(Schema.schema("[request] " + apiName))
                        .responseSchema(Schema.schema("[response] " + docIdentifier))
                        .build()
                    )
                )
            );
    }

    private void performErrorDocument(
        Long fileId,
        ResultMatcher status,
        String docIdentifier,
        String authorization
    ) throws Exception {
        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/files/{fileId}/download", fileId)
                    .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary("파일 다운로드 API")
                        .description("파일에 대한 접근 권한을 검증하고, 클라이언트가 직접 다운로드할 수 있는 파일 URL을 응답하는 API 입니다. <br>"
                            + "테스트시 우측 자물쇠를 클릭하여 유효한 인증 토큰을 입력해야 정상 테스트가 가능합니다.")
                        .pathParameters(
                            parameterWithName("fileId").description("다운로드할 파일 ID")
                        )
                        .requestHeaders(
                            headerWithName("Authorization").description("인증 토큰")
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
                )
            );
    }
}
