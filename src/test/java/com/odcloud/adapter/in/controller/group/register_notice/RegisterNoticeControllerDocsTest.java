package com.odcloud.adapter.in.controller.group.register_notice;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.port.in.RegisterNoticeUseCase;
import com.odcloud.application.service.register_notice.RegisterNoticeServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class RegisterNoticeControllerDocsTest extends RestDocsSupport {

    private final RegisterNoticeUseCase useCase = mock(RegisterNoticeUseCase.class);
    private final String apiName = "그룹 공지사항 등록 API";

    @Override
    protected Object initController() {
        return new RegisterNoticeController(useCase);
    }

    @Nested
    @DisplayName("[register] 그룹 공지사항을 등록하는 API")
    class Describe_register {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            RegisterNoticeRequest request = RegisterNoticeRequest.builder()
                .title("공지사항 제목")
                .content("공지사항 내용입니다.")
                .build();
            String authorization = "error token";
            given(useCase.register(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument("testGroup", request, authorization, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 유효한 정보로 공지사항을 등록한다")
        void success() throws Exception {
            // given
            RegisterNoticeRequest request = RegisterNoticeRequest.builder()
                .title("공지사항 제목")
                .content("공지사항 내용입니다.")
                .build();

            RegisterNoticeServiceResponse serviceResponse =
                RegisterNoticeServiceResponse.ofSuccess();

            given(useCase.register(any())).willReturn(serviceResponse);

            // when & then
            performDocument("testGroup", request, "Bearer test", status().isOk(), "success",
                "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("등록 성공 여부")
            );
        }

        @Test
        @DisplayName("[success] 제목이 null인 경우 자동으로 날짜 기반 제목이 생성된다")
        void success_autoGenerateTitle() throws Exception {
            // given
            RegisterNoticeRequest request = RegisterNoticeRequest.builder()
                .title(null)
                .content("공지사항 내용입니다.")
                .build();

            RegisterNoticeServiceResponse serviceResponse =
                RegisterNoticeServiceResponse.ofSuccess();

            given(useCase.register(any())).willReturn(serviceResponse);

            // when & then
            performDocument("testGroup", request, "Bearer test", status().isOk(),
                "제목 자동 생성 성공", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("등록 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 내용이 빈 문자열인 경우 400 에러를 반환한다")
        void error_contentIsBlank() throws Exception {
            // given
            RegisterNoticeRequest request = RegisterNoticeRequest.builder()
                .title("공지사항 제목")
                .content(null)
                .build();

            // when & then
            performErrorDocument("testGroup", request, "Bearer test", status().isBadRequest(),
                "내용 미입력");
        }

        @Test
        @DisplayName("[error] 그룹의 소유자가 아닌 경우 500 에러를 반환한다")
        void error_notGroupOwner() throws Exception {
            // given
            RegisterNoticeRequest request = RegisterNoticeRequest.builder()
                .title("공지사항 제목")
                .content("공지사항 내용입니다.")
                .build();

            given(useCase.register(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_INVALID_GROUP_OWNER));

            // when & then
            performErrorDocument("testGroup", request, "Bearer test",
                status().isInternalServerError(),
                "그룹 소유자가 아님");
        }
    }

    private void performDocument(
        String groupId,
        RegisterNoticeRequest request,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        JsonFieldType titleType = request.title() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType contentType = request.content() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;

        mockMvc.perform(post("/groups/{groupId}/notices", groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Group")
                    .summary("그룹 공지사항 등록 API")
                    .description("그룹장이 공지사항을 등록하고 그룹원들에게 푸시 알림을 전송하는 API 입니다")
                    .pathParameters(
                        parameterWithName("groupId").description("그룹 ID")
                    )
                    .requestFields(
                        fieldWithPath("title").type(titleType)
                            .description("공지사항 제목"),
                        fieldWithPath("content").type(contentType)
                            .description("공지사항 내용")
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
        String groupId,
        RegisterNoticeRequest request,
        String authorization,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(groupId, request, authorization, status, identifier, "error",
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
