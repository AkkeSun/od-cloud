package com.odcloud.adapter.in.find_pending_group_accounts;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.port.in.FindPendingGroupAccountsUseCase;
import com.odcloud.application.service.find_pending_group_accounts.FindPendingGroupAccountsServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class FindPendingGroupAccountsControllerDocsTest extends RestDocsSupport {

    private final FindPendingGroupAccountsUseCase useCase = mock(
        FindPendingGroupAccountsUseCase.class);
    private final String apiName = "그룹 오너의 PENDING 상태 사용자 목록 조회 API";

    @Override
    protected Object initController() {
        return new FindPendingGroupAccountsController(useCase);
    }

    @Nested
    @DisplayName("[findPendingAccounts] 그룹 오너의 PENDING 상태 사용자 목록 조회 API")
    class Describe_findPendingAccounts {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            String authorization = "error token";
            given(useCase.findPendingAccounts(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performDocument(authorization, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력", "error",
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

        @Test
        @DisplayName("[success] 오너인 그룹의 PENDING 상태 사용자 목록을 그룹별로 조회한다")
        void success() throws Exception {
            // given
            FindPendingGroupAccountsServiceResponse.PendingAccountInfo pending1 =
                FindPendingGroupAccountsServiceResponse.PendingAccountInfo.builder()
                    .accountId(10L)
                    .nickname("User1")
                    .name("홍길동")
                    .email("user1@example.com")
                    .requestDate(LocalDateTime.of(2025, 1, 1, 10, 0))
                    .build();

            FindPendingGroupAccountsServiceResponse.PendingAccountInfo pending2 =
                FindPendingGroupAccountsServiceResponse.PendingAccountInfo.builder()
                    .accountId(11L)
                    .nickname("User2")
                    .name("김철수")
                    .email("user2@example.com")
                    .requestDate(LocalDateTime.of(2025, 1, 2, 11, 0))
                    .build();

            FindPendingGroupAccountsServiceResponse.PendingAccountInfo pending3 =
                FindPendingGroupAccountsServiceResponse.PendingAccountInfo.builder()
                    .accountId(12L)
                    .nickname("User3")
                    .name("이영희")
                    .email("user3@example.com")
                    .requestDate(LocalDateTime.of(2025, 1, 3, 12, 0))
                    .build();

            FindPendingGroupAccountsServiceResponse.GroupPendingAccounts group1 =
                FindPendingGroupAccountsServiceResponse.GroupPendingAccounts.builder()
                    .groupId("group-1")
                    .groupName("Test Group 1")
                    .pendingAccounts(List.of(pending1, pending2))
                    .build();

            FindPendingGroupAccountsServiceResponse.GroupPendingAccounts group2 =
                FindPendingGroupAccountsServiceResponse.GroupPendingAccounts.builder()
                    .groupId("group-2")
                    .groupName("Test Group 2")
                    .pendingAccounts(List.of(pending3))
                    .build();

            FindPendingGroupAccountsServiceResponse serviceResponse =
                new FindPendingGroupAccountsServiceResponse(List.of(group1, group2));

            given(useCase.findPendingAccounts(any())).willReturn(serviceResponse);

            // when & then
            performDocument("Bearer test", status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.groups").type(JsonFieldType.ARRAY)
                    .description("PENDING 상태 사용자가 있는 그룹 목록"),
                fieldWithPath("data.groups[].groupId").type(JsonFieldType.STRING)
                    .description("그룹 ID"),
                fieldWithPath("data.groups[].groupName").type(JsonFieldType.STRING)
                    .description("그룹명"),
                fieldWithPath("data.groups[].pendingAccounts").type(JsonFieldType.ARRAY)
                    .description("PENDING 상태 사용자 목록"),
                fieldWithPath("data.groups[].pendingAccounts[].accountId").type(
                        JsonFieldType.NUMBER)
                    .description("사용자 ID"),
                fieldWithPath("data.groups[].pendingAccounts[].name").type(
                        JsonFieldType.STRING)
                    .description("사용자 이름"),
                fieldWithPath("data.groups[].pendingAccounts[].email").type(
                        JsonFieldType.STRING)
                    .description("사용자 이메일"),
                fieldWithPath("data.groups[].pendingAccounts[].nickname").type(
                        JsonFieldType.STRING)
                    .description("사용자 닉네임"),
                fieldWithPath("data.groups[].pendingAccounts[].requestDate").type(
                        JsonFieldType.STRING)
                    .description("가입 요청 날짜")
            );
        }

        @Test
        @DisplayName("[success] PENDING 상태 사용자가 없으면 빈 목록을 반환한다")
        void success_emptyList() throws Exception {
            // given
            FindPendingGroupAccountsServiceResponse serviceResponse =
                new FindPendingGroupAccountsServiceResponse(List.of());

            given(useCase.findPendingAccounts(any())).willReturn(serviceResponse);

            // when & then
            performDocument("Bearer test", status().isOk(), "빈 목록 조회", "success-empty",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.groups").type(JsonFieldType.ARRAY)
                    .description("PENDING 상태 사용자가 있는 그룹 목록 (빈 배열)")
            );
        }
    }

    private void performDocument(
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        org.springframework.restdocs.payload.FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(get("/groups/pending-accounts")
                .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Group")
                    .summary("그룹 오너의 PENDING 상태 사용자 목록 조회 API")
                    .description("현재 로그인한 사용자가 오너로 있는 그룹의 PENDING 상태 사용자 목록을 조회하는 API입니다.<br><br>"
                        + "- 그룹별로 묶어서 응답합니다.<br>"
                        + "- 각 사용자의 닉네임, 아이디(accountId), 요청날짜(requestDate)를 포함합니다.<br>"
                        + "- PENDING 상태 사용자가 없는 그룹은 응답에 포함되지 않습니다.")
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }
}
