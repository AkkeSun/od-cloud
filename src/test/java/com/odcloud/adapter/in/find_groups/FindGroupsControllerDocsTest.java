package com.odcloud.adapter.in.find_groups;

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
import com.odcloud.application.port.in.FindGroupsUseCase;
import com.odcloud.application.service.find_groups.FindGroupsServiceResponse;
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
        @DisplayName("[error] 권한 정보가 없는 사용자가 API를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            String authorization = "error token";
            given(useCase.findAll(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument("인증 토큰 미입력 혹은 만료된 토큰 입력",
                authorization, status().isUnauthorized());
        }

        @Test
        @DisplayName("[success] 사용자가 속한 모든 그룹 목록을 조회한다")
        void success_findAllGroups() throws Exception {
            // given
            String authorization = "Bearer test";

            FindGroupsServiceResponse.GroupResponseItem group1 =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-1")
                    .ownerEmail("owner1@example.com")
                    .description("Development Team")
                    .regDt("2024-01-01T12:00:00")
                    .status("ACTIVE")
                    .build();

            FindGroupsServiceResponse.GroupResponseItem group2 =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-2")
                    .ownerEmail("owner2@example.com")
                    .description("Marketing Team")
                    .regDt("2024-01-02T12:00:00")
                    .status("PENDING")
                    .build();

            FindGroupsServiceResponse.GroupResponseItem group3 =
                FindGroupsServiceResponse.GroupResponseItem.builder()
                    .id("group-3")
                    .ownerEmail("owner3@example.com")
                    .description("Sales Team")
                    .regDt("2024-01-03T12:00:00")
                    .status("ACTIVE")
                    .build();

            FindGroupsServiceResponse serviceResponse = FindGroupsServiceResponse.builder()
                .groups(List.of(group1, group2, group3))
                .build();

            given(useCase.findAll(any())).willReturn(serviceResponse);

            // when & then
            performDocument("그룹 목록 조회 성공", "success", authorization,
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
                    .type(JsonFieldType.STRING).description("등록일시"),
                fieldWithPath("data.groups[].status")
                    .type(JsonFieldType.STRING).description("사용자의 그룹 가입 상태 (ACTIVE, PENDING 등)")
            );
        }
    }

    private void performDocument(
        String identifier, String responseSchema, String authorization, ResultMatcher status,
        FieldDescriptor... responseFields) throws Exception {

        mockMvc.perform(RestDocumentationRequestBuilders.get("/groups")
                .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, identifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("Account")
                        .summary("그룹 목록 조회 API")
                        .description("사용자가 속한 모든 그룹 목록을 조회하는 API 입니다.<br><br>"
                            + "- 각 그룹에 대한 사용자의 가입 상태(status)를 함께 제공합니다.<br>"
                            + "- ACTIVE: 승인된 멤버<br>"
                            + "- PENDING: 가입 대기 중<br>"
                            + "- null: 미가입 <br><br>"
                            + "테스트시 우측 자물쇠를 클릭하여 유효한 인증 토큰을 입력해야 정상 테스트가 가능합니다.")
                        .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                        .responseFields(responseFields)
                        .requestSchema(Schema.schema("[request] " + apiName))
                        .responseSchema(Schema.schema("[response] " + responseSchema))
                        .build()
                    )
                )
            );
    }

    private void performErrorDocument(String identifier, String authorization,
        ResultMatcher status) throws Exception {
        performDocument(identifier, "error", authorization, status,
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
