package com.odcloud.adapter.in.controller.subscription.find_group_subscriptions;

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
import com.odcloud.application.subscription.port.in.FindGroupSubscriptionsUseCase;
import com.odcloud.application.subscription.service.find_group_subscriptions.FindGroupSubscriptionsResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
import com.odcloud.infrastructure.resolver.LoginAccountResolver;
import com.odcloud.infrastructure.util.JwtUtil;
import io.jsonwebtoken.Claims;
import java.time.LocalDate;
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
class FindGroupSubscriptionsControllerDocsTest {

    private final FindGroupSubscriptionsUseCase useCase = mock(FindGroupSubscriptionsUseCase.class);
    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final String apiName = "그룹 구독 조회 API";

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Account mockAccount = Account.builder()
            .id(1L)
            .email("user@example.com")
            .groups(List.of(
                Group.builder().id(1L).name("개발팀").build(),
                Group.builder().id(2L).name("마케팅팀").build()
            ))
            .build();

        Claims mockClaims = mock(Claims.class);
        given(jwtUtil.getClaims(any())).willReturn(mockClaims);
        given(mockClaims.getSubject()).willReturn("user@example.com");
        given(mockClaims.get("id")).willReturn(1L);
        given(mockClaims.get("nickname")).willReturn("홍길동");
        given(mockClaims.get("picture")).willReturn("http://example.com/picture.jpg");
        given(mockClaims.get("groups")).willReturn(List.of(
            java.util.Map.of("id", 1, "name", "개발팀"),
            java.util.Map.of("id", 2, "name", "마케팅팀")
        ));

        this.mockMvc = MockMvcBuilders
            .standaloneSetup(new FindGroupSubscriptionsController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setCustomArgumentResolvers(new LoginAccountResolver(jwtUtil))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
            .build();
    }

    @Nested
    @DisplayName("[find] 그룹별 활성 구독 조회 API")
    class Describe_find {

        @Test
        @DisplayName("[success] 상품별 구독 목록을 조회한다")
        void success() throws Exception {
            // given
            FindGroupSubscriptionsResponse.GroupInfo devGroup =
                FindGroupSubscriptionsResponse.GroupInfo.builder()
                    .groupId(1L)
                    .groupName("개발팀")
                    .subscriptionId(1000L)
                    .buyer("홍길동")
                    .status("ACTIVE")
                    .expiredDate(LocalDate.of(2026, 8, 1))
                    .build();

            FindGroupSubscriptionsResponse.GroupInfo marketingGroup =
                FindGroupSubscriptionsResponse.GroupInfo.builder()
                    .groupId(2L)
                    .groupName("마케팅팀")
                    .subscriptionId(1001L)
                    .buyer("김철수")
                    .status("EXP_PENDING")
                    .expiredDate(LocalDate.of(2026, 8, 1))
                    .build();

            FindGroupSubscriptionsResponse response = FindGroupSubscriptionsResponse.builder()
                .productId(100L)
                .productName("CLOUD_100GB")
                .groups(List.of(devGroup, marketingGroup))
                .build();

            given(useCase.find(any())).willReturn(List.of(response));

            // when & then
            performDocument("상품별 구독 조회 성공", "success", status().isOk(),
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.ARRAY).description("응답 데이터"),
                fieldWithPath("data[].productId")
                    .type(JsonFieldType.NUMBER).description("상품 ID"),
                fieldWithPath("data[].productName")
                    .type(JsonFieldType.STRING).description("상품명"),
                fieldWithPath("data[].groups")
                    .type(JsonFieldType.ARRAY).description("해당 상품을 구독중인 그룹 목록"),
                fieldWithPath("data[].groups[].groupId")
                    .type(JsonFieldType.NUMBER).description("그룹 ID"),
                fieldWithPath("data[].groups[].groupName")
                    .type(JsonFieldType.STRING).description("그룹명"),
                fieldWithPath("data[].groups[].subscriptionId")
                    .type(JsonFieldType.NUMBER).description("구독 ID"),
                fieldWithPath("data[].groups[].buyer")
                    .type(JsonFieldType.STRING).description("구매자 닉네임"),
                fieldWithPath("data[].groups[].status")
                    .type(JsonFieldType.STRING).description("구독 상태 (ACTIVE, EXP_PENDING)"),
                fieldWithPath("data[].groups[].expiredDate")
                    .type(JsonFieldType.STRING).description("구독 만료 일시")
            );
        }
    }

    private void performDocument(
        String identifier, String responseSchema, ResultMatcher status,
        FieldDescriptor... responseFields) throws Exception {

        mockMvc.perform(RestDocumentationRequestBuilders.get("/subscriptions/active")
                .header("Authorization", "Bearer test-token-123"))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, identifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Subscription")
                    .summary("그룹 구독 조회 API")
                    .description("로그인한 사용자가 속한 그룹의 활성 구독 목록을 상품 기준으로 조회합니다.<br><br>"
                        + "- 각 상품마다 해당 상품을 구독중인 그룹 목록이 포함됩니다.<br>"
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
            ));
    }
}
