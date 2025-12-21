package com.odcloud.adapter.in.controller.download_files;

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
import com.odcloud.application.port.in.DownloadFilesUseCase;
import com.odcloud.application.service.download_files.DownloadFilesServiceResponse;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DownloadFilesControllerDocsTest extends RestDocsSupport {

    private final DownloadFilesUseCase useCase = mock(DownloadFilesUseCase.class);
    private final String apiName = "여러 파일 다운로드 API";

    @Override
    protected Object initController() {
        return new DownloadFilesController(useCase);
    }

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        this.objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        this.objectMapper.registerModule(
            new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.objectMapper.disable(
            com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
            .setControllerAdvice(new com.odcloud.infrastructure.exception.ExceptionAdvice())
            .setMessageConverters(
                new ResourceHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter(objectMapper)
            )
            .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
            .build();
    }

    @Nested
    @DisplayName("[downloadFiles] 여러 파일을 압축하여 다운로드 API")
    class Describe_downloadFiles {

        @Test
        @DisplayName("[success] 여러 파일을 ZIP으로 압축하여 다운로드한다")
        void success() throws Exception {
            // given
            String authorization = "Bearer test";
            List<Long> fileIds = List.of(1L, 2L, 3L);
            Resource resource = new ByteArrayResource("Mock ZIP content".getBytes());

            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "files_" + timestamp + ".zip";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build());

            DownloadFilesServiceResponse serviceResponse =
                new DownloadFilesServiceResponse(resource, headers);

            given(useCase.download(any())).willReturn(serviceResponse);

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
        @DisplayName("[error] 파일 다운로드 오류 시 500 코드와 에러 메시지를 응답한다")
        void error_downloadError() throws Exception {
            // given
            String authorization = "Bearer test";

            given(useCase.download(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_FILE_DOWNLOAD_ERROR));

            // when & then
            performErrorDocument(status().isInternalServerError(), List.of(1L, 2L), "파일 다운로드 에러",
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
                        .summary("파일 목록 다운로드 API")
                        .description("파일 목록을 ZIP으로 압축하여 다운로드하는 API 입니다. <br>"
                            + "압축 파일명 형식: files_yyyyMMdd_HHmmss.zip <br>"
                            + "테스트시 우측 자물쇠를 클릭하여 유효한 인증 토큰을 입력해야 정상 테스트가 가능합니다.")
                        .queryParameters(
                            parameterWithName("fileIds").description("다운로드할 파일 ID 목록 (최소 1개 이상)")
                        )
                        .requestHeaders(
                            headerWithName("Authorization").description("인증 토큰")
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
                        .summary("파일 목록 다운로드 API")
                        .description("파일 목록을 ZIP으로 압축하여 다운로드하는 API 입니다. <br>"
                            + "압축 파일명 형식: files_yyyyMMdd_HHmmss.zip <br>"
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
