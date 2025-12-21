package com.odcloud.adapter.in.controller.find_group_account_list;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.port.in.FindGroupAccountListUseCase;
import com.odcloud.application.service.find_group_account_list.FindGroupAccountListServiceResponse;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class FindGroupAccountListControllerDocsTest extends RestDocsSupport {

    private final FindGroupAccountListUseCase useCase = mock(FindGroupAccountListUseCase.class);
    private final String apiName = "그룹 계정 목록 조회 API";

    @Override
    protected Object initController() {
        return new FindGroupAccountListController(useCase);
    }

    @Nested
    @DisplayName("[findGroupAccountList] 그룹에 속한 계정 목록을 조회하는 API")
    class Describe_findGroupAccountList {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error() throws Exception {
            // given
            given(useCase.findGroupAccountList(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument("Bearer test", "TEST", status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 유효한 그룹 ID로 계정 목록을 조회한다")
        void success() throws Exception {
            // given
            String groupId = "group-123";
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);

            List<GroupAccount> groupAccounts = List.of(
                GroupAccount.builder()
                    .id(1L)
                    .groupId(groupId)
                    .accountId(100L)
                    .name("홍길동")
                    .nickName("gildong")
                    .email("hong@example.com")
                    .status("ACTIVE")
                    .modDt(now)
                    .regDt(now.minusDays(5))
                    .build(),
                GroupAccount.builder()
                    .id(2L)
                    .groupId(groupId)
                    .accountId(200L)
                    .name("김철수")
                    .nickName("chulsoo")
                    .email("kim@example.com")
                    .status("PENDING")
                    .modDt(now.minusDays(1))
                    .regDt(now.minusDays(2))
                    .build()
            );

            FindGroupAccountListServiceResponse serviceResponse =
                FindGroupAccountListServiceResponse.of(groupAccounts);

            given(useCase.findGroupAccountList(groupId)).willReturn(serviceResponse);

            // when & then
            performDocument("Bearer test", groupId, status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.groupAccounts").type(JsonFieldType.ARRAY)
                    .description("그룹 계정 목록"),
                fieldWithPath("data.groupAccounts[].id").type(JsonFieldType.NUMBER)
                    .description("그룹 계정 ID"),
                fieldWithPath("data.groupAccounts[].groupId").type(JsonFieldType.STRING)
                    .description("그룹 ID"),
                fieldWithPath("data.groupAccounts[].accountId").type(JsonFieldType.NUMBER)
                    .description("계정 ID"),
                fieldWithPath("data.groupAccounts[].name").type(JsonFieldType.STRING)
                    .description("사용자 이름"),
                fieldWithPath("data.groupAccounts[].nickName").type(JsonFieldType.STRING)
                    .description("사용자 닉네임"),
                fieldWithPath("data.groupAccounts[].email").type(JsonFieldType.STRING)
                    .description("사용자 이메일"),
                fieldWithPath("data.groupAccounts[].status").type(JsonFieldType.STRING)
                    .description("계정 상태 (ACTIVE /  PENDING / BLOCK)"),
                fieldWithPath("data.groupAccounts[].updateDt").type(JsonFieldType.STRING)
                    .description("수정 일시"),
                fieldWithPath("data.groupAccounts[].regDt").type(JsonFieldType.STRING)
                    .description("등록 일시")
            );
        }
    }

    private void performDocument(
        String authorization,
        String groupId,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(get("/groups/{groupId}/accounts", groupId)
                .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Account")
                    .summary("그룹 계정 목록 조회 API")
                    .description("특정 그룹에 속한 계정 목록을 조회합니다.")
                    .pathParameters(
                        parameterWithName("groupId")
                            .description("그룹 ID")
                    )
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        String authorization,
        String groupId,
        ResultMatcher status,
        String docIdentifier
    ) throws Exception {
        performDocument(authorization, groupId, status, docIdentifier, "error",
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
