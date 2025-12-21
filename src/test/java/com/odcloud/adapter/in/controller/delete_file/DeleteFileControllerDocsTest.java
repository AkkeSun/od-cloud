package com.odcloud.adapter.in.controller.delete_file;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.port.in.DeleteFileUseCase;
import com.odcloud.application.service.delete_file.DeleteFileServiceResponse;
import com.odcloud.application.service.delete_file.DeleteFileServiceResponseItem;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class DeleteFileControllerDocsTest extends RestDocsSupport {

    private final DeleteFileUseCase useCase = mock(DeleteFileUseCase.class);
    private final String apiName = "파일 삭제 API";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected Object initController() {
        return new DeleteFileController(useCase);
    }

    @Nested
    @DisplayName("[delete] 파일 삭제 API")
    class Describe_delete {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            String authorization = "error token";
            String requestBody = objectMapper.writeValueAsString(
                DeleteFileRequest.builder()
                    .fileIds(List.of(1L))
                    .build()
            );

            given(useCase.deleteFile(any()))
                .willThrow(
                    new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when & then
            performErrorDocument(status().isUnauthorized(), "인증 토큰 미입력 혹은 만료된 토큰 입력",
                authorization, requestBody);
        }

        @Test
        @DisplayName("[success] 단일 파일 삭제에 성공한다")
        void success_singleFile() throws Exception {
            // given
            String authorization = "Bearer test";
            String requestBody = objectMapper.writeValueAsString(
                DeleteFileRequest.builder()
                    .fileIds(List.of(1L))
                    .build()
            );

            DeleteFileServiceResponse serviceResponse = DeleteFileServiceResponse.builder()
                .result(true)
                .logs(List.of(
                    DeleteFileServiceResponseItem.builder()
                        .fileId(1L)
                        .errorMessage(null)
                        .build()
                ))
                .build();

            given(useCase.deleteFile(any())).willReturn(serviceResponse);

            // when & then
            performDocument(status().isOk(), "success", "success", authorization, requestBody,
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.result")
                    .type(JsonFieldType.BOOLEAN).description("전체 삭제 성공 여부"),
                fieldWithPath("data.logs")
                    .type(JsonFieldType.ARRAY).description("파일별 삭제 결과 로그"),
                fieldWithPath("data.logs[].fileId")
                    .type(JsonFieldType.NUMBER).description("파일 ID"),
                fieldWithPath("data.logs[].errorMessage")
                    .type(JsonFieldType.NULL).description("에러 메시지 (성공 시 null)")
            );
        }

        @Test
        @DisplayName("[success] 여러 파일 삭제에 성공한다")
        void success_multipleFiles() throws Exception {
            // given
            String authorization = "Bearer test";
            String requestBody = objectMapper.writeValueAsString(
                DeleteFileRequest.builder()
                    .fileIds(List.of(1L, 2L, 3L))
                    .build()
            );

            DeleteFileServiceResponse serviceResponse = DeleteFileServiceResponse.builder()
                .result(true)
                .logs(List.of(
                    DeleteFileServiceResponseItem.builder().fileId(1L).errorMessage(null).build(),
                    DeleteFileServiceResponseItem.builder().fileId(2L).errorMessage(null).build(),
                    DeleteFileServiceResponseItem.builder().fileId(3L).errorMessage(null).build()
                ))
                .build();

            given(useCase.deleteFile(any())).willReturn(serviceResponse);

            // when & then
            performDocument(status().isOk(), "success_multiple", "success", authorization,
                requestBody,
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.result")
                    .type(JsonFieldType.BOOLEAN).description("전체 삭제 성공 여부"),
                fieldWithPath("data.logs")
                    .type(JsonFieldType.ARRAY).description("파일별 삭제 결과 로그"),
                fieldWithPath("data.logs[].fileId")
                    .type(JsonFieldType.NUMBER).description("파일 ID"),
                fieldWithPath("data.logs[].errorMessage")
                    .type(JsonFieldType.NULL).description("에러 메시지 (성공 시 null)")
            );
        }

        @Test
        @DisplayName("[partial_success] 일부 파일만 삭제 성공한다")
        void partialSuccess() throws Exception {
            // given
            String authorization = "Bearer test";
            String requestBody = objectMapper.writeValueAsString(
                DeleteFileRequest.builder()
                    .fileIds(List.of(1L, 2L))
                    .build()
            );

            DeleteFileServiceResponse serviceResponse = DeleteFileServiceResponse.builder()
                .result(false)
                .logs(List.of(
                    DeleteFileServiceResponseItem.builder()
                        .fileId(1L)
                        .errorMessage(null)
                        .build(),
                    DeleteFileServiceResponseItem.builder()
                        .fileId(2L)
                        .errorMessage("조회된 파일이 없습니다")
                        .build()
                ))
                .build();

            given(useCase.deleteFile(any())).willReturn(serviceResponse);

            // when & then
            performDocument(status().isOk(), "partial_success", "partial_success", authorization,
                requestBody,
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.result")
                    .type(JsonFieldType.BOOLEAN).description("전체 삭제 성공 여부 (일부 실패 시 false)"),
                fieldWithPath("data.logs")
                    .type(JsonFieldType.ARRAY).description("파일별 삭제 결과 로그"),
                fieldWithPath("data.logs[].fileId")
                    .type(JsonFieldType.NUMBER).description("파일 ID"),
                fieldWithPath("data.logs[].errorMessage")
                    .optional().description("에러 메시지 (성공 시 null, 실패 시 메시지)")
            );
        }

        @Test
        @DisplayName("[error] 조회된 파일이 없는 경우 200 코드지만 result가 false이다")
        void error_fileDoesNotExist() throws Exception {
            // given
            String authorization = "Bearer test";
            String requestBody = objectMapper.writeValueAsString(
                DeleteFileRequest.builder()
                    .fileIds(List.of(999L))
                    .build()
            );

            DeleteFileServiceResponse serviceResponse = DeleteFileServiceResponse.builder()
                .result(false)
                .logs(List.of(
                    DeleteFileServiceResponseItem.builder()
                        .fileId(999L)
                        .errorMessage("조회된 파일이 없습니다")
                        .build()
                ))
                .build();

            given(useCase.deleteFile(any())).willReturn(serviceResponse);

            // when & then
            performDocument(status().isOk(), "error_file_not_found", "error", authorization,
                requestBody,
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.result")
                    .type(JsonFieldType.BOOLEAN).description("전체 삭제 성공 여부 (false)"),
                fieldWithPath("data.logs")
                    .type(JsonFieldType.ARRAY).description("파일별 삭제 결과 로그"),
                fieldWithPath("data.logs[].fileId")
                    .type(JsonFieldType.NUMBER).description("파일 ID"),
                fieldWithPath("data.logs[].errorMessage")
                    .type(JsonFieldType.STRING).description("에러 메시지")
            );
        }

        @Test
        @DisplayName("[error] 접근 권한이 없는 경우 200 코드지만 result가 false이다")
        void error_forbiddenAccess() throws Exception {
            // given
            String authorization = "Bearer test";
            String requestBody = objectMapper.writeValueAsString(
                DeleteFileRequest.builder()
                    .fileIds(List.of(1L))
                    .build()
            );

            DeleteFileServiceResponse serviceResponse = DeleteFileServiceResponse.builder()
                .result(false)
                .logs(List.of(
                    DeleteFileServiceResponseItem.builder()
                        .fileId(1L)
                        .errorMessage("접근 권한이 없습니다")
                        .build()
                ))
                .build();

            given(useCase.deleteFile(any())).willReturn(serviceResponse);

            // when & then
            performDocument(status().isOk(), "error_forbidden", "error", authorization, requestBody,
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.result")
                    .type(JsonFieldType.BOOLEAN).description("전체 삭제 성공 여부 (false)"),
                fieldWithPath("data.logs")
                    .type(JsonFieldType.ARRAY).description("파일별 삭제 결과 로그"),
                fieldWithPath("data.logs[].fileId")
                    .type(JsonFieldType.NUMBER).description("파일 ID"),
                fieldWithPath("data.logs[].errorMessage")
                    .type(JsonFieldType.STRING).description("에러 메시지")
            );
        }
    }

    private void performDocument(
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        String authorization,
        String requestBody,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/files")
                    .header("Authorization", authorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary("파일 삭제 API")
                        .description("여러 파일을 한 번에 삭제하는 API 입니다. <br>"
                            + "PRIVATE 폴더: 폴더 소유주만 파일 삭제 가능<br>"
                            + "PUBLIC 폴더: 해당 폴더에 접근 가능한 사용자만 파일 삭제 가능<br>"
                            + "일부 파일만 삭제 성공해도 200 응답이지만 result는 false가 됩니다.<br>"
                            + "테스트시 우측 자물쇠를 클릭하여 유효한 인증 토큰을 입력해야 정상 테스트가 가능합니다.")
                        .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                        .requestFields(
                            fieldWithPath("fileIds").type(JsonFieldType.ARRAY)
                                .description("삭제할 파일 ID 목록")
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
        ResultMatcher status,
        String identifier,
        String authorization,
        String requestBody
    ) throws Exception {
        mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/files")
                    .header("Authorization", authorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, identifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary("파일 삭제 API")
                        .description("파일 삭제 API 에러 케이스")
                        .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                        .requestFields(
                            fieldWithPath("fileIds").type(JsonFieldType.ARRAY)
                                .description("삭제할 파일 ID 목록")
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
