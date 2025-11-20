package com.odcloud.adapter.in.find_groups;

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
import com.odcloud.application.port.in.FindGroupsUseCase;
import com.odcloud.application.service.find_groups.FindGroupsServiceResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class FindGroupsControllerDocsTest extends RestDocsSupport {

    private final FindGroupsUseCase useCase = mock(FindGroupsUseCase.class);
    private final String apiName = "그룹 목록 조회 API";

    @Override
    protected Object initController() {
        return new FindGroupsController(useCase);
    }

    @Nested
    @DisplayName("[findAll] 그룹 목록 조회 API")
    class Describe_findAll {

        @Test
        @DisplayName("[success] keyword가 'all'인 경우 모든 그룹 목록을 조회한다")
        void success_findAllGroups() throws Exception {
            // given
            FindGroupsServiceResponse.GroupResponseItem group1 =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-1")
                    .ownerEmail("owner1@example.com")
                    .description("Development Team")
                    .regDt("2024-01-01T12:00:00")
                    .build();

            FindGroupsServiceResponse.GroupResponseItem group2 =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-2")
                    .ownerEmail("owner2@example.com")
                    .description("Marketing Team")
                    .regDt("2024-01-02T12:00:00")
                    .build();

            FindGroupsServiceResponse.GroupResponseItem group3 =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-3")
                    .ownerEmail("owner3@example.com")
                    .description("Sales Team")
                    .regDt("2024-01-03T12:00:00")
                    .build();

            FindGroupsServiceResponse serviceResponse = FindGroupsServiceResponse.builder()
                .groups(List.of(group1, group2, group3))
                .build();

            given(useCase.findAll(any())).willReturn(serviceResponse);

            // when & then
            performDocument("그룹 목록 조회 성공 (all)", "success_all", "all",
                status().isOk(),
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.groups")
                    .type(JsonFieldType.ARRAY).description("그룹 목록"),
                fieldWithPath("data.groups[].id")
                    .type(JsonFieldType.STRING).description("그룹 ID"),
                fieldWithPath("data.groups[].ownerEmail")
                    .type(JsonFieldType.STRING).description("그룹 소유자 이메일"),
                fieldWithPath("data.groups[].description")
                    .type(JsonFieldType.STRING).description("그룹 설명"),
                fieldWithPath("data.groups[].regDt")
                    .type(JsonFieldType.STRING).description("등록일시")
            );
        }

        @Test
        @DisplayName("[success] keyword로 Description을 LIKE 검색한다")
        void success_searchByKeyword() throws Exception {
            // given
            FindGroupsServiceResponse.GroupResponseItem group1 =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-1")
                    .ownerEmail("owner1@example.com")
                    .description("개발팀")
                    .regDt("2024-01-01T12:00:00")
                    .build();

            FindGroupsServiceResponse.GroupResponseItem group2 =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-3")
                    .ownerEmail("owner3@example.com")
                    .description("개발 지원팀")
                    .regDt("2024-01-03T12:00:00")
                    .build();

            FindGroupsServiceResponse serviceResponse = FindGroupsServiceResponse.builder()
                .groups(List.of(group1, group2))
                .build();

            given(useCase.findAll(any())).willReturn(serviceResponse);

            // when & then
            performDocument("그룹 목록 조회 성공 (keyword 검색)", "success_keyword", "개발",
                status().isOk(),
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.groups")
                    .type(JsonFieldType.ARRAY).description("그룹 목록"),
                fieldWithPath("data.groups[].id")
                    .type(JsonFieldType.STRING).description("그룹 ID"),
                fieldWithPath("data.groups[].ownerEmail")
                    .type(JsonFieldType.STRING).description("그룹 소유자 이메일"),
                fieldWithPath("data.groups[].description")
                    .type(JsonFieldType.STRING).description("그룹 설명"),
                fieldWithPath("data.groups[].regDt")
                    .type(JsonFieldType.STRING).description("등록일시")
            );
        }
    }

    private void performDocument(
        String identifier, String responseSchema, String keyword, ResultMatcher status,
        FieldDescriptor... responseFields) throws Exception {

        mockMvc.perform(RestDocumentationRequestBuilders.get("/groups")
                .param("keyword", keyword))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, identifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("Group")
                        .summary("그룹 목록 조회 API")
                        .description("그룹 목록을 조회하는 API 입니다.<br><br>"
                            + "- keyword='all': 모든 그룹 조회<br>"
                            + "- keyword≠'all': 그룹 Description LIKE 검색<br><br>"
                            + "인증 토큰 없이 사용 가능합니다.")
                        .queryParameters(
                            parameterWithName("keyword").description("검색 키워드 (필수)")
                        )
                        .responseFields(responseFields)
                        .requestSchema(Schema.schema("[request] " + apiName))
                        .responseSchema(Schema.schema("[response] " + responseSchema))
                        .build()
                    )
                )
            );
    }
}
