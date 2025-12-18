package com.odcloud.adapter.in.find_group_self;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odcloud.application.port.in.FindGroupSelfUseCase;
import com.odcloud.application.service.find_group_self.FindGroupSelfServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
import com.odcloud.infrastructure.util.JwtUtil;
import com.odcloud.resolver.LoginAccountResolver;
import io.jsonwebtoken.Claims;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(RestDocumentationExtension.class)
class FindGroupSelfControllerDocsTest {

    private final FindGroupSelfUseCase useCase = mock(FindGroupSelfUseCase.class);
    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final String apiName = "현재 사용자의 그룹 정보 조회 API";

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Account mockAccount = Account.builder()
            .id(1L)
            .email("test@example.com")
            .groups(List.of(
                Group.builder().id("group-1").name("Test Group 1").build(),
                Group.builder().id("group-2").name("Test Group 2").build()
            ))
            .build();

        Claims mockClaims = mock(Claims.class);
        given(jwtUtil.getClaims(any())).willReturn(mockClaims);
        given(mockClaims.getSubject()).willReturn("test@example.com");
        given(mockClaims.get("id")).willReturn(1L);
        given(mockClaims.get("nickname")).willReturn("Test User");
        given(mockClaims.get("picture")).willReturn("http://example.com/picture.jpg");
        given(mockClaims.get("groups")).willReturn(List.of(
            java.util.Map.of("id", "group-1", "name", "Test Group 1"),
            java.util.Map.of("id", "group-2", "name", "Test Group 2")
        ));

        this.mockMvc = MockMvcBuilders.standaloneSetup(new FindGroupSelfController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setCustomArgumentResolvers(new LoginAccountResolver(jwtUtil))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
            .build();
    }

    @Nested
    @DisplayName("[findSelf] 현재 사용자의 그룹 정보 조회 API")
    class Describe_findSelf {

        @Test
        @DisplayName("[success] 가입된 그룹과 대기중인 그룹을 분리하여 조회한다")
        void success_findSelfGroups() throws Exception {
            // given
            FindGroupSelfServiceResponse.MemberInfo manager1 =
                FindGroupSelfServiceResponse.MemberInfo.builder()
                    .nickname("Manager")
                    .email("manager@example.com")
                    .build();

            FindGroupSelfServiceResponse.MemberInfo member1 =
                FindGroupSelfServiceResponse.MemberInfo.builder()
                    .nickname("Member1")
                    .email("member1@example.com")
                    .build();

            FindGroupSelfServiceResponse.MemberInfo member2 =
                FindGroupSelfServiceResponse.MemberInfo.builder()
                    .nickname("Member2")
                    .email("member2@example.com")
                    .build();

            FindGroupSelfServiceResponse.ActiveGroupInfo activeGroup1 =
                FindGroupSelfServiceResponse.ActiveGroupInfo.builder()
                    .id("activeGroup-1")
                    .name("My Team")
                    .manager(manager1)
                    .members(List.of(member1, member2))
                    .activeMemberCount(3)
                    .build();

            FindGroupSelfServiceResponse.MemberInfo manager2 =
                FindGroupSelfServiceResponse.MemberInfo.builder()
                    .nickname("OtherManager")
                    .email("other@example.com")
                    .build();

            FindGroupSelfServiceResponse.MemberInfo member3 =
                FindGroupSelfServiceResponse.MemberInfo.builder()
                    .nickname("Member3")
                    .email("member3@example.com")
                    .build();

            FindGroupSelfServiceResponse.ActiveGroupInfo activeGroup2 =
                FindGroupSelfServiceResponse.ActiveGroupInfo.builder()
                    .id("activeGroup-2")
                    .name("Development Team")
                    .manager(manager2)
                    .members(List.of(member3))
                    .activeMemberCount(2)
                    .build();

            FindGroupSelfServiceResponse.PendingGroupInfo pendingGroup =
                FindGroupSelfServiceResponse.PendingGroupInfo.builder()
                    .id("pendingGroup")
                    .name("Marketing Team")
                    .activeMemberCount(1)
                    .build();

            FindGroupSelfServiceResponse serviceResponse = FindGroupSelfServiceResponse.builder()
                .activeGroups(List.of(activeGroup1, activeGroup2))
                .pendingGroups(List.of(pendingGroup))
                .build();

            given(useCase.findSelf(any())).willReturn(serviceResponse);

            // when & then
            performDocument("현재 사용자의 그룹 정보 조회 성공", "success",
                "Bearer test-token-123",
                status().isOk(),
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.activeGroups")
                    .type(JsonFieldType.ARRAY).description("가입된 그룹 목록"),
                fieldWithPath("data.activeGroups[].id")
                    .type(JsonFieldType.STRING).description("그룹 아이디"),
                fieldWithPath("data.activeGroups[].name")
                    .type(JsonFieldType.STRING).description("그룹명"),
                fieldWithPath("data.activeGroups[].manager")
                    .type(JsonFieldType.OBJECT).description("매니저 정보").optional(),
                fieldWithPath("data.activeGroups[].manager.nickname")
                    .type(JsonFieldType.STRING).description("매니저 닉네임").optional(),
                fieldWithPath("data.activeGroups[].manager.email")
                    .type(JsonFieldType.STRING).description("매니저 이메일").optional(),
                fieldWithPath("data.activeGroups[].members")
                    .type(JsonFieldType.ARRAY).description("구성원 목록 (매니저 제외)"),
                fieldWithPath("data.activeGroups[].members[].nickname")
                    .type(JsonFieldType.STRING).description("구성원 닉네임"),
                fieldWithPath("data.activeGroups[].members[].email")
                    .type(JsonFieldType.STRING).description("구성원 이메일"),
                fieldWithPath("data.activeGroups[].activeMemberCount")
                    .type(JsonFieldType.NUMBER).description("활성 사용자 수"),
                fieldWithPath("data.pendingGroups")
                    .type(JsonFieldType.ARRAY).description("가입 대기중인 그룹 목록"),
                fieldWithPath("data.pendingGroups[].id")
                    .type(JsonFieldType.STRING).description("그룹 아이디"),
                fieldWithPath("data.pendingGroups[].name")
                    .type(JsonFieldType.STRING).description("그룹명"),
                fieldWithPath("data.pendingGroups[].activeMemberCount")
                    .type(JsonFieldType.NUMBER).description("활성 사용자 수")
            );
        }
    }

    private void performDocument(
        String identifier, String responseSchema, String authorization, ResultMatcher status,
        FieldDescriptor... responseFields) throws Exception {

        mockMvc.perform(RestDocumentationRequestBuilders.get("/groups/self")
                .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, identifier),
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("Account")
                        .summary("현재 사용자의 그룹 정보 조회 API")
                        .description("현재 인증된 사용자가 속한 그룹의 정보를 조회하는 API 입니다.<br><br>"
                            + "- 가입된 그룹(activeGroups)과 가입 대기중인 그룹(pendingGroups)을 분리하여 반환합니다.<br>"
                            + "- 가입된 그룹: 그룹명, 매니저 정보, 구성원 목록(닉네임, 이메일), 활성 사용자 수, 내가 매니저인지 여부를 포함합니다.<br>"
                            + "- 대기중인 그룹: 그룹명, 활성 사용자 수, 내가 매니저인지 여부만 포함합니다.<br>"
                            + "- 구성원 목록은 ACTIVE 상태이며 매니저를 제외한 멤버들입니다.<br>"
                            + "- 매니저인 그룹이 목록의 앞쪽에 정렬됩니다.<br>"
                            + "- 인증 토큰(JWT)이 필수입니다.")
                        .requestHeaders(
                            headerWithName("Authorization")
                                .description("인증 토큰 (Bearer {token})")
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
