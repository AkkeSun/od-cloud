package com.odcloud.adapter.in.controller.file.delete_folder;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
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
import com.odcloud.application.file.port.in.DeleteFolderUseCase;
import com.odcloud.application.file.service.delete_folder.DeleteFolderServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class DeleteFolderControllerDocsTest extends RestDocsSupport {

    private final DeleteFolderUseCase useCase = mock(DeleteFolderUseCase.class);
    private final String apiName = "폴더 삭제 API";

    @Override
    protected Object initController() {
        return new DeleteFolderController(useCase);
    }

    @Nested
    @DisplayName("[delete] 폴더 삭제 API")
    class Describe_delete {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            String authorization = "error token";
            Long folderId = 1L;

            willThrow(new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY))
                .given(useCase).deleteFolder(any(), anyLong());

            // when & then
            performErrorDocument(status().isUnauthorized(), "인증 토큰 미입력 혹은 만료된 토큰 입력",
                authorization, folderId);
        }

        @Test
        @DisplayName("[success] 폴더 삭제에 성공한다")
        void success() throws Exception {
            // given
            String authorization = "Bearer test";
            Long folderId = 1L;

            given(useCase.deleteFolder(any(), anyLong()))
                .willReturn(DeleteFolderServiceResponse.ofSuccess());

            // when & then
            performDocument(status().isOk(), "success", "success", authorization, folderId,
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.result")
                    .type(JsonFieldType.BOOLEAN).description("삭제 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 조회된 폴더가 없는 경우 500 코드와 에러 메시지를 응답한다")
        void error_folderDoesNotExist() throws Exception {
            // given
            String authorization = "Bearer test";
            Long folderId = 999L;

            willThrow(new CustomBusinessException(ErrorCode.Business_DoesNotExists_FOLDER))
                .given(useCase).deleteFolder(any(), anyLong());

            // when & then
            performErrorDocument(status().isInternalServerError(), "error_folder_not_found",
                authorization, folderId);
        }

        @Test
        @DisplayName("[error] 접근 권한이 없는 경우 403 코드와 에러 메시지를 응답한다")
        void error_forbiddenAccess() throws Exception {
            // given
            String authorization = "Bearer test";
            Long folderId = 1L;

            willThrow(new CustomAuthorizationException(ErrorCode.ACCESS_DENIED))
                .given(useCase).deleteFolder(any(), anyLong());

            // when & then
            performErrorDocument(status().isForbidden(), "error_forbidden",
                authorization, folderId);
        }
    }

    private void performDocument(
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        String authorization,
        Long folderId,
        org.springframework.restdocs.payload.FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/folders/{folderId}", folderId)
                    .header("Authorization", authorization)
                    .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary("폴더 삭제 API")
                        .description("폴더와 하위 폴더 및 모든 파일을 삭제하는 API 입니다. <br>"
                            + "폴더 소유주만 삭제 가능합니다.<br>"
                            + "해당 폴더 내의 모든 하위 폴더와 파일이 함께 삭제됩니다.<br>"
                            + "테스트시 우측 자물쇠를 클릭하여 유효한 인증 토큰을 입력해야 정상 테스트가 가능합니다.")
                        .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                        .pathParameters(
                            parameterWithName("folderId").description("삭제할 폴더 ID")
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
        Long folderId
    ) throws Exception {
        mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/folders/{folderId}", folderId)
                    .header("Authorization", authorization)
                    .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, identifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary("폴더 삭제 API")
                        .description("폴더 삭제 API 에러 케이스")
                        .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                        .pathParameters(
                            parameterWithName("folderId").description("삭제할 폴더 ID")
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
