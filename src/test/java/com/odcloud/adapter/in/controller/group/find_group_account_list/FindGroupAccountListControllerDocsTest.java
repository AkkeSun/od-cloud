package com.odcloud.adapter.in.controller.group.find_group_account_list;

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
import com.odcloud.application.group.port.in.FindGroupAccountListUseCase;
import com.odcloud.application.group.service.find_group_account_list.FindGroupAccountListResponse;
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
            performErrorDocument("Bearer test", 1L, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 유효한 그룹 ID로 계정 목록을 조회한다")
        void success() throws Exception {
            // given
            Long groupId = 1L;
            LocalDateTime now = LocalDateTime.of(2025, 11, 14, 10, 0);

            List<GroupAccount> groupAccounts = List.of(
                GroupAccount.builder()
                    .id(1L)
                    .groupId(groupId)
                    .accountId(100L)
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
                    .nickName("chulsoo")
                    .email("kim@example.com")
                    .status("PENDING")
                    .modDt(now.minusDays(1))
                    .regDt(now.minusDays(2))
                    .build()
            );

            FindGroupAccountListResponse Response =
                FindGroupAccountListResponse.of(groupAccounts);

            given(useCase.findGroupAccountList(groupId)).willReturn(Response);

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
                fieldWithPath("data.groupAccounts[].groupId").type(JsonFieldType.NUMBER)
                    .description("그룹 ID"),
                fieldWithPath("data.groupAccounts[].accountId").type(JsonFieldType.NUMBER)
                    .description("계정 ID"),
                fieldWithPath("data.groupAccounts[].groupName").type(JsonFieldType.STRING)
                    .description("그룹명").optional(),
                fieldWithPath("data.groupAccounts[].groupOwner").type(JsonFieldType.STRING)
                    .description("그룹 오너 이메일").optional(),
                fieldWithPath("data.groupAccounts[].nickName").type(JsonFieldType.STRING)
                    .description("사용자 닉네임").optional(),
                fieldWithPath("data.groupAccounts[].email").type(JsonFieldType.STRING)
                    .description("사용자 이메일").optional(),
                fieldWithPath("data.groupAccounts[].picture").type(JsonFieldType.STRING)
                    .description("프로필 사진").optional(),
                fieldWithPath("data.groupAccounts[].status").type(JsonFieldType.STRING)
                    .description("계정 상태 (ACTIVE / PENDING / BLOCK)").optional(),
                fieldWithPath("data.groupAccounts[].memo").type(JsonFieldType.STRING)
                    .description("메모").optional(),
                fieldWithPath("data.groupAccounts[].showYn").type(JsonFieldType.STRING)
                    .description("노출 여부").optional(),
                fieldWithPath("data.groupAccounts[].modDt").type(JsonFieldType.STRING)
                    .description("수정 일시").optional(),
                fieldWithPath("data.groupAccounts[].regDt").type(JsonFieldType.STRING)
                    .description("등록 일시").optional(),
                fieldWithPath("data.groupAccounts[].block").type(JsonFieldType.BOOLEAN)
                    .description("차단 여부"),
                fieldWithPath("data.groupAccounts[].active").type(JsonFieldType.BOOLEAN)
                    .description("활성 여부")
            );
        }
    }

    private void performDocument(
        String authorization,
        Long groupId,
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
                    .tag("Group")
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
        Long groupId,
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
