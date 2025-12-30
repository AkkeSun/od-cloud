package com.odcloud.adapter.in.controller.group.find_group;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
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
import com.odcloud.application.port.in.FindGroupUseCase;
import com.odcloud.application.service.find_group.FindGroupServiceResponse;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import com.odcloud.infrastructure.exception.ExceptionAdvice;
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
class FindGroupControllerDocsTest {

    private final FindGroupUseCase useCase = mock(FindGroupUseCase.class);
    private final String apiName = "그룹 상세 조회 API";

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.mockMvc = MockMvcBuilders.standaloneSetup(new FindGroupController(useCase))
            .setControllerAdvice(new ExceptionAdvice())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
            .build();
    }

    @Nested
    @DisplayName("[findById] 그룹 상세 조회 API")
    class Describe_findById {

        @Test
        @DisplayName("[success] 그룹 ID로 그룹 상세 정보를 조회한다")
        void success() throws Exception {
            // given
            String groupId = "test-group";

            FindGroupServiceResponse.MemberInfo manager =
                FindGroupServiceResponse.MemberInfo.builder()
                    .nickname("Manager")
                    .email("manager@example.com")
                    .build();

            FindGroupServiceResponse.MemberInfo member1 =
                FindGroupServiceResponse.MemberInfo.builder()
                    .nickname("Member1")
                    .email("member1@example.com")
                    .build();

            FindGroupServiceResponse.MemberInfo member2 =
                FindGroupServiceResponse.MemberInfo.builder()
                    .nickname("Member2")
                    .email("member2@example.com")
                    .build();

            FindGroupServiceResponse.NoticeInfo notice1 =
                FindGroupServiceResponse.NoticeInfo.builder()
                    .id(5L)
                    .title("12월 27일 공지사항")
                    .content("공지사항 내용입니다.")
                    .build();

            FindGroupServiceResponse.NoticeInfo notice2 =
                FindGroupServiceResponse.NoticeInfo.builder()
                    .id(4L)
                    .title("중요 공지")
                    .content("중요한 내용입니다.")
                    .build();

            FindGroupServiceResponse response = FindGroupServiceResponse.builder()
                .id(groupId)
                .name("My Team")
                .manager(manager)
                .members(List.of(member1, member2))
                .activeMemberCount(3)
                .notices(List.of(notice1, notice2))
                .build();

            given(useCase.findById(any())).willReturn(response);

            // when & then
            performDocument(groupId, "그룹 상세 조회 성공", "success", status().isOk(),
                fieldWithPath("httpStatus")
                    .type(JsonFieldType.NUMBER).description("상태 코드"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING).description("상태 메시지"),
                fieldWithPath("data")
                    .type(JsonFieldType.OBJECT).description("응답 데이터"),
                fieldWithPath("data.id")
                    .type(JsonFieldType.STRING).description("그룹 ID"),
                fieldWithPath("data.name")
                    .type(JsonFieldType.STRING).description("그룹명"),
                fieldWithPath("data.manager")
                    .type(JsonFieldType.OBJECT).description("그룹장 정보"),
                fieldWithPath("data.manager.nickname")
                    .type(JsonFieldType.STRING).description("그룹장 닉네임"),
                fieldWithPath("data.manager.email")
                    .type(JsonFieldType.STRING).description("그룹장 이메일"),
                fieldWithPath("data.members")
                    .type(JsonFieldType.ARRAY).description("그룹원 목록 (그룹장 제외)"),
                fieldWithPath("data.members[].nickname")
                    .type(JsonFieldType.STRING).description("그룹원 닉네임"),
                fieldWithPath("data.members[].email")
                    .type(JsonFieldType.STRING).description("그룹원 이메일"),
                fieldWithPath("data.activeMemberCount")
                    .type(JsonFieldType.NUMBER).description("활성 멤버 수 (그룹장 포함)"),
                fieldWithPath("data.notices")
                    .type(JsonFieldType.ARRAY).description("최근 공지사항 목록 (최대 5개)"),
                fieldWithPath("data.notices[].id")
                    .type(JsonFieldType.NUMBER).description("공지사항 ID"),
                fieldWithPath("data.notices[].title")
                    .type(JsonFieldType.STRING).description("공지사항 제목"),
                fieldWithPath("data.notices[].content")
                    .type(JsonFieldType.STRING).description("공지사항 내용")
            );
        }

        @Test
        @DisplayName("[error] 존재하지 않는 그룹 ID인 경우 500 에러를 반환한다")
        void error_groupNotFound() throws Exception {
            // given
            String groupId = "non-existent-group";

            given(useCase.findById(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_DoesNotExists_GROUP));

            // when & then
            performErrorDocument(groupId, "존재하지 않는 그룹 ID", status().isInternalServerError());
        }
    }

    private void performDocument(
        String groupId,
        String docIdentifier,
        String responseSchema,
        ResultMatcher status,
        FieldDescriptor... responseFields
    ) throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/groups/{groupId}", groupId))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Group")
                    .summary("그룹 상세 조회 API")
                    .description("그룹 ID로 그룹의 상세 정보를 조회합니다. 그룹장, 멤버 목록, 최근 공지사항(최대 5개)을 포함합니다.")
                    .pathParameters(
                        parameterWithName("groupId").description("그룹 ID")
                    )
                    .responseFields(responseFields)
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        String groupId,
        String identifier,
        ResultMatcher status
    ) throws Exception {
        performDocument(groupId, identifier, "error", status,
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
