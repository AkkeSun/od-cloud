package com.odcloud.adapter.in.controller.voucher.find_group_vouchers;

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
import com.odcloud.application.voucher.port.in.FindGroupVouchersUseCase;
import com.odcloud.application.voucher.service.find_group_vouchers.FindGroupVouchersResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
import com.odcloud.infrastructure.resolver.LoginAccountResolver;
import com.odcloud.infrastructure.util.JwtUtil;
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
class FindGroupVouchersControllerDocsTest {

    private final FindGroupVouchersUseCase useCase = mock(FindGroupVouchersUseCase.class);
    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final String apiName = "그룹 바우처 조회 API";

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
            .standaloneSetup(new FindGroupVouchersController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setCustomArgumentResolvers(new LoginAccountResolver(jwtUtil))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
            .build();
    }

    @Nested
    @DisplayName("[find] 그룹별 활성 바우처 조회 API")
    class Describe_find {

        @Test
        @DisplayName("[success] 그룹별 바우처 목록을 조회한다")
        void success() throws Exception {
            // given
            FindGroupVouchersResponse.VoucherItem voucher1 =
                FindGroupVouchersResponse.VoucherItem.builder()
                    .voucherName("CLOUD_100GB")
                    .payer("홍길동")
                    .expiredAt("2025-12-31T00:00:00")
                    .build();

            FindGroupVouchersResponse.VoucherItem voucher2 =
                FindGroupVouchersResponse.VoucherItem.builder()
                    .voucherName("CLOUD_50GB")
                    .payer("김철수")
                    .expiredAt(null)
                    .build();

            FindGroupVouchersResponse.GroupVouchers devGroup =
                FindGroupVouchersResponse.GroupVouchers.builder()
                    .groupName("개발팀")
                    .vouchers(List.of(voucher1, voucher2))
                    .build();

            FindGroupVouchersResponse.GroupVouchers marketingGroup =
                FindGroupVouchersResponse.GroupVouchers.builder()
                    .groupName("마케팅팀")
                    .vouchers(List.of())
                    .build();

            FindGroupVouchersResponse response = FindGroupVouchersResponse.builder()
                .groups(List.of(devGroup, marketingGroup))
                .build();

            given(useCase.find(any())).willReturn(response);

            // when & then
            performDocument("그룹 바우처 조회 성공", "success", status().isOk(),
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.groups")
                    .type(JsonFieldType.ARRAY).description("그룹별 바우처 목록"),
                fieldWithPath("data.groups[].groupName")
                    .type(JsonFieldType.STRING).description("그룹명"),
                fieldWithPath("data.groups[].vouchers")
                    .type(JsonFieldType.ARRAY).description("바우처 목록 (바우처가 없으면 빈 배열)"),
                fieldWithPath("data.groups[].vouchers[].voucherName")
                    .type(JsonFieldType.STRING).description("바우처명").optional(),
                fieldWithPath("data.groups[].vouchers[].payer")
                    .type(JsonFieldType.STRING).description("결제자 닉네임").optional(),
                fieldWithPath("data.groups[].vouchers[].expiredAt")
                    .type(JsonFieldType.STRING).description("만료일시 (null 가능)").optional()
            );
        }
    }

    private void performDocument(
        String identifier, String responseSchema, ResultMatcher status,
        FieldDescriptor... responseFields) throws Exception {

        mockMvc.perform(RestDocumentationRequestBuilders.get("/vouchers/active")
                .header("Authorization", "Bearer test-token-123"))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, identifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Voucher")
                    .summary("그룹 바우처 조회 API")
                    .description("로그인한 사용자가 속한 그룹별 활성 바우처 목록을 조회합니다.<br><br>"
                        + "- 바우처가 없는 그룹도 빈 배열로 응답됩니다.<br>"
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
