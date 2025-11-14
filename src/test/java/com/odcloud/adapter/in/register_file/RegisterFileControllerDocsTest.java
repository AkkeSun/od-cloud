package com.odcloud.adapter.in.register_file;

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
import com.odcloud.application.port.in.RegisterFileUseCase;
import com.odcloud.application.service.register_file.RegisterFileServiceResponse;
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

class RegisterFileControllerDocsTest extends RestDocsSupport {

    private final RegisterFileUseCase useCase = mock(RegisterFileUseCase.class);
    private final String apiName = "파일 등록 API";

    @Override
    protected Object initController() {
        return new RegisterFileController(useCase);
    }

    @Nested
    @DisplayName("[register] 파일 등록 API")
    class Describe_register {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error() throws Exception {
            // given
            Long folderId = 1L;
            MockMultipartFile file = new MockMultipartFile(
                "files", "test1.txt", "text/plain", "test file content 1".getBytes()
            );
            String authorization = "error token";
            given(useCase.register(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument(folderId, status().isUnauthorized(), "인증 토큰 미입력 혹은 만료된 토큰 입력",
                authorization, new MockMultipartFile[]{file});
        }

        @Test
        @DisplayName("[success] 파일 등록에 성공한다")
        void success() throws Exception {
            // given
            Long folderId = 1L;
            MockMultipartFile file1 = new MockMultipartFile(
                "files", "test1.txt", "text/plain", "test file content 1".getBytes()
            );
            MockMultipartFile file2 = new MockMultipartFile(
                "files", "test2.txt", "text/plain", "test file content 2".getBytes()
            );
            String authorization = "Bearer test";

            RegisterFileServiceResponse serviceResponse =
                new RegisterFileServiceResponse(true);

            given(useCase.register(any())).willReturn(serviceResponse);

            // when & then
            performDocument(folderId, status().isOk(), "success", "success", authorization,
                new MockMultipartFile[]{file1, file2},
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.result")
                    .type(JsonFieldType.BOOLEAN).description("등록 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 폴더 아이디가 null 인 경우 400 코드와 에러 메시지를 응답한다")
        void error_folderIdIsNull() throws Exception {
            String authorization = "Bearer test";

            MockMultipartFile file = new MockMultipartFile(
                "files", "test.txt", "text/plain", "test file content".getBytes()
            );

            performErrorDocument(null, status().isBadRequest(), "폴더 아이디 미입력", authorization,
                new MockMultipartFile[]{file});
        }

        @Test
        @DisplayName("[error] 조회된 상위 폴더가 없는 경우 500 코드와 에러 메시지를 응답한다.")
        void error_folderDoesNotExist() throws Exception {
            Long folderId = 999L;
            String authorization = "Bearer test";
            MockMultipartFile file = new MockMultipartFile(
                "files", "test.txt", "text/plain", "test file content".getBytes()
            );

            given(useCase.register(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_FOLDER
                ));

            performErrorDocument(folderId, status().isInternalServerError(), "조회된 상위 폴더 없음",
                authorization, new MockMultipartFile[]{file});
        }

        @Test
        @DisplayName("[error] 파일 업로드 오류시 500 코드와 에러 메시지를 응답한다.")
        void error_fileUploadError() throws Exception {
            Long folderId = 1L;
            String authorization = "Bearer test";
            MockMultipartFile file = new MockMultipartFile(
                "files", "test.txt", "text/plain", "test file content".getBytes()
            );

            given(useCase.register(any()))
                .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                    com.odcloud.infrastructure.exception.ErrorCode.Business_FILE_UPLOAD_ERROR
                ));

            performErrorDocument(folderId, status().isInternalServerError(), "파일 업로드 에러",
                authorization, new MockMultipartFile[]{file});
        }
    }

    private void performDocument(
        Long folderId,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        String authorization,
        MockMultipartFile[] files,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/files")
                    .file(files[0])
                    .param("folderId", folderId == null ? "" : folderId.toString())
                    .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary("파일 등록 API")
                        .description("파일을 등록하는 API 입니다. <br>"
                            + "RestDocs API 문서 작성 모듈 특성상 Multipart 입력 파라미터 정보 기록에 한계가 있어 아래 목록을 참고하시어 요청 바랍니다. <br><br>"
                            + "[입력받는 멀티파트 파라미터 목록]<br>"
                            + "- files : 업로드 파일 목록 <br>"
                            + "- folderId : 폴더 아이디 <br><br>"
                            + "테스트시 우측 자물쇠를 클릭하여 유효한 인증 토큰을 입력해야 정상 테스트가 가능합니다. <br>"
                            + "(요청 헤더에 인증 토큰을 입력하여 테스트하지 않습니다)")
                        .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                        .responseFields(responseFields)
                        .requestSchema(Schema.schema("[request] " + apiName))
                        .responseSchema(Schema.schema("[response] " + responseSchema))
                        .build()
                    )
                )
            );
    }


    private void performErrorDocument(
        Long folderId,
        ResultMatcher status,
        String identifier,
        String authorization,
        MockMultipartFile[] files
    ) throws Exception {
        performDocument(folderId, status, identifier, "error", authorization, files,
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
