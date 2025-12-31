package com.odcloud.adapter.in.controller.file.find_files;

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
import com.odcloud.application.file.port.in.FindFilesUseCase;
import com.odcloud.application.file.service.find_files.FindFilesServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class FindFilesControllerDocsTest extends RestDocsSupport {

    private final FindFilesUseCase useCase = mock(FindFilesUseCase.class);
    private final String apiName = "파일 및 폴더 목록 조회 API";

    @Override
    protected Object initController() {
        return new FindFilesController(useCase);
    }

    @Nested
    @DisplayName("[findAll] 파일 및 폴더 목록 조회 API")
    class Describe_findAll {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            String authorization = "error token";
            Long folderId = 1L;
            given(useCase.findAll(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument("인증 토큰 미입력 혹은 만료된 토큰 입력",
                authorization, folderId, null, null, null, status().isUnauthorized());
        }

        @Test
        @DisplayName("[success] folderId로 파일 및 폴더 목록을 조회한다")
        void success_byFolderId() throws Exception {
            // given
            String authorization = "Bearer test";
            Long folderId = 1L;
            String sortType = "NAME_ASC";

            FindFilesServiceResponse.FileResponseItem file1 =
                FindFilesServiceResponse.FileResponseItem.builder()
                    .id(1L)
                    .name("test1.txt")
                    .fileLoc("/group1/test1.txt")
                    .regDt("2024-01-01 12:00:00")
                    .build();

            FindFilesServiceResponse.FileResponseItem file2 =
                FindFilesServiceResponse.FileResponseItem.builder()
                    .id(2L)
                    .name("test2.pdf")
                    .fileLoc("/group1/test2.pdf")
                    .regDt("2024-01-02 12:00:00")
                    .build();

            FindFilesServiceResponse.FolderResponseItem folder1 =
                FindFilesServiceResponse.FolderResponseItem.builder()
                    .id(11L)
                    .name("Documents")
                    .groupId("group1")
                    .regDt("2024-01-01 12:00:00")
                    .build();

            FindFilesServiceResponse.FolderResponseItem folder2 =
                FindFilesServiceResponse.FolderResponseItem.builder()
                    .id(12L)
                    .name("Private")
                    .groupId("group1")
                    .regDt("2024-01-02 12:00:00")
                    .build();

            FindFilesServiceResponse serviceResponse = FindFilesServiceResponse.builder()
                .parentFolderId(folderId)
                .files(List.of(file1, file2))
                .folders(List.of(folder1, folder2))
                .build();

            given(useCase.findAll(any())).willReturn(serviceResponse);

            // when & then
            performDocument("folderId로 조회 성공", "success-by-folderId", authorization,
                folderId, null, null, sortType, status().isOk(),
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.parentFolderId")
                    .type(JsonFieldType.NUMBER).description("부모 폴더 ID"),
                fieldWithPath("data.folders")
                    .type(JsonFieldType.ARRAY).description("폴더 목록"),
                fieldWithPath("data.folders[].id")
                    .type(JsonFieldType.NUMBER).description("폴더 ID"),
                fieldWithPath("data.folders[].name")
                    .type(JsonFieldType.STRING).description("폴더 이름"),
                fieldWithPath("data.folders[].groupId")
                    .type(JsonFieldType.STRING).description("그룹 ID"),
                fieldWithPath("data.folders[].regDt")
                    .type(JsonFieldType.STRING).description("등록일시"),
                fieldWithPath("data.files")
                    .type(JsonFieldType.ARRAY).description("파일 목록"),
                fieldWithPath("data.files[].id")
                    .type(JsonFieldType.NUMBER).description("파일 ID"),
                fieldWithPath("data.files[].name")
                    .type(JsonFieldType.STRING).description("파일 이름"),
                fieldWithPath("data.files[].fileLoc")
                    .type(JsonFieldType.STRING).description("파일 경로"),
                fieldWithPath("data.files[].regDt")
                    .type(JsonFieldType.STRING).description("등록일시")
            );
        }

        @Test
        @DisplayName("[error] 유효하지 않은 sortType 입력 시 400 코드와 에러 메시지를 응답한다.")
        void error_invalidSortType() throws Exception {
            // given
            String authorization = "Bearer test";
            Long folderId = 1L;
            String invalidSortType = "INVALID_SORT";

            // when & then
            performErrorDocument("유효하지 않은 정렬 타입",
                authorization, folderId, null, null, invalidSortType, status().isBadRequest());
        }
    }

    private void performDocument(
        String identifier,
        String responseSchema,
        String authorization,
        Long folderId,
        String groupId,
        String keyword,
        String sortType,
        ResultMatcher status,
        FieldDescriptor... responseFields
    ) throws Exception {
        var requestBuilder = RestDocumentationRequestBuilders.get("/files")
            .header("Authorization", authorization);

        if (folderId != null) {
            requestBuilder.param("folderId", folderId.toString());
        }
        if (groupId != null) {
            requestBuilder.param("groupId", groupId);
        }
        if (keyword != null) {
            requestBuilder.param("keyword", keyword);
        }
        if (sortType != null) {
            requestBuilder.param("sortType", sortType);
        }

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, identifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("File")
                        .summary("파일 및 폴더 목록 조회 API")
                        .description("파일 및 폴더 목록을 조회하는 API 입니다.<br><br>"
                            + "[검색 방법]<br>"
                            + "1. folderId로 검색: 특정 폴더 내의 파일 및 하위 폴더 조회<br>"
                            + "2. keyword로 검색: 키워드가 포함된 파일/폴더 검색<br>"
                            + "3. groupId로 필터링: 특정 그룹의 폴더만 필터링<br><br>"
                            + "[정렬 옵션]<br>"
                            + "- NAME_ASC: 이름 오름차순<br>"
                            + "- NAME_DESC: 이름 내림차순<br>"
                            + "- REG_DT_ASC: 등록일시 오름차순<br>"
                            + "- REG_DT_DESC: 등록일시 내림차순<br><br>"
                            + "테스트시 우측 자물쇠를 클릭하여 유효한 인증 토큰을 입력해야 정상 테스트가 가능합니다.")
                        .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                        .queryParameters(
                            parameterWithName("folderId").description("폴더 ID").optional(),
                            parameterWithName("groupId").description("그룹 ID").optional(),
                            parameterWithName("keyword").description("검색 키워드").optional(),
                            parameterWithName("sortType").description(
                                "정렬 타입 (NAME_ASC / NAME_DESC / REG_DT_ASC / REG_DT_DESC)").optional()
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
        String identifier,
        String authorization,
        Long folderId,
        String groupId,
        String keyword,
        String sortType,
        ResultMatcher status
    ) throws Exception {
        performDocument(identifier, "error", authorization, folderId, groupId, keyword, sortType,
            status,
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
