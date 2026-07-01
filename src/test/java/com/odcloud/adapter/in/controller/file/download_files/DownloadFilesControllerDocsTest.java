package com.odcloud.adapter.in.controller.file.download_files;

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
import com.odcloud.application.file.port.in.DownloadFilesUseCase;
import com.odcloud.application.file.service.download_files.DownloadFilesResponse;
import com.odcloud.application.file.service.download_files.DownloadFilesResponse.DownloadFileItem;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class DownloadFilesControllerDocsTest extends RestDocsSupport {

    private final DownloadFilesUseCase useCase = mock(DownloadFilesUseCase.class);
    private final String apiName = "여러 파일 다운로드 API";

    @Override
    protected Object initController() {
        return new DownloadFilesController(useCase);
    }

    @Nested
    @DisplayName("[downloadFiles] 여러 파일 다운로드 API")
    class Describe_downloadFiles {

        @Test
        @DisplayName("[success] 접근 권한 검증 후 파일 URL 목록을 응답한다")
        void success() throws Exception {
            // given
            String authorization = "Bearer test";
            List<Long> fileIds = List.of(1L, 2L, 3L);

            DownloadFilesResponse response = DownloadFilesResponse.builder()
                .files(List.of(
                    DownloadFileItem.builder()
                        .fileId(1L)
                        .fileName("test1.txt")
                        .fileUrl("https://moimism.odlab.kr/test-group/folder1/test1.txt")
                        .build(),
                    DownloadFileItem.builder()
                        .fileId(2L)
                        .fileName("test2.pdf")
                        .fileUrl("https://moimism.odlab.kr/test-group/folder1/test2.pdf")
                        .build(),
                    DownloadFileItem.builder()
                        .fileId(3L)
                        .fileName("test3.jpg")
                        .fileUrl("https://moimism.odlab.kr/test-group/folder1/test3.jpg")
                        .build()
                ))
                .build();

            given(useCase.download(any())).willReturn(response);

            // when & then
            performDocument(status().isOk(), fileIds, "success", authorization);
        }

        @Test
        @DisplayName("[error] 파일 ID 목록이 비어있으면 400 코드와 에러 메시지를 응답한다")
        void error_emptyFileIds() throws Exception {
            // given
            String authorization = "Bearer test";

            // when & then
            performErrorDocument(status().isBadRequest(), new ArrayList<>(), "빈 파일 ID 목록",
                authorization);
        }

        @Test
        @DisplayName("[error] 존재하지 않는 파일 ID로 다운로드 시 500 코드와 에러 메시지를 응답한다")
        void error_fileNotFound() throws Exception {
            // given
            String authorization = "Bearer test";

            given(useCase.download(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_DoesNotExists_FILE));

            // when & then
            performErrorDocument(status().isInternalServerError(), List.of(1L, 2L), "파일 조회 실패",
                authorization);
        }

        @Test
        @DisplayName("[error] 접근 권한이 없는 파일이 포함된 경우 에러 메시지를 응답한다")
        void error_accessDenied() throws Exception {
            // given
            String authorization = "Bearer test";

            given(useCase.download(any()))
                .willThrow(new CustomAuthorizationException(ErrorCode.ACCESS_DENIED));

            // when & then
            performErrorDocument(status().isForbidden(), List.of(1L, 2L), "접근 권한 없음",
                authorization);
        }
    }

    private void performDocument(
        ResultMatcher status,
        List<Long> fileIds,
        String docIdentifier,
        String authorization
    ) throws Exception {
        StringBuilder uri = new StringBuilder("/files/download");
        if (!fileIds.isEmpty()) {
            uri.append("?fileIds=");
            for (Long fileId : fileIds) {
                uri.append(fileId).append("&fileIds=");
            }
        }

        mockMvc.perform(
                RestDocumentationRequestBuilders.get(uri.toString())
                    .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary("여러 파일 다운로드 API")
                        .description("여러 파일에 대한 접근 권한을 검증하고, 클라이언트가 직접 다운로드할 수 있는 "
                            + "파일 URL 목록을 응답하는 API 입니다. <br>"
                            + "테스트시 우측 자물쇠를 클릭하여 유효한 인증 토큰을 입력해야 정상 테스트가 가능합니다.")
                        .queryParameters(
                            parameterWithName("fileIds").description("다운로드할 파일 ID 목록 (최소 1개 이상)")
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
                            fieldWithPath("data.files").type(JsonFieldType.ARRAY)
                                .description("파일 목록"),
                            fieldWithPath("data.files[].fileId").type(JsonFieldType.NUMBER)
                                .description("파일 ID"),
                            fieldWithPath("data.files[].fileName").type(JsonFieldType.STRING)
                                .description("파일명"),
                            fieldWithPath("data.files[].fileUrl").type(JsonFieldType.STRING)
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
        ResultMatcher status,
        List<Long> fileIds,
        String docIdentifier,
        String authorization
    ) throws Exception {
        StringBuilder uri = new StringBuilder("/files/download?fileIds=");
        if (!fileIds.isEmpty()) {
            for (Long fileId : fileIds) {
                uri.append(fileId).append("&fileIds=");
            }
        }

        mockMvc.perform(
                RestDocumentationRequestBuilders.get(uri.toString())
                    .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary("여러 파일 다운로드 API")
                        .description("여러 파일에 대한 접근 권한을 검증하고, 클라이언트가 직접 다운로드할 수 있는 "
                            + "파일 URL 목록을 응답하는 API 입니다. <br>"
                            + "테스트시 우측 자물쇠를 클릭하여 유효한 인증 토큰을 입력해야 정상 테스트가 가능합니다.")
                        .queryParameters(
                            parameterWithName("fileIds").description("다운로드할 파일 ID 목록 (최소 1개 이상)")
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
