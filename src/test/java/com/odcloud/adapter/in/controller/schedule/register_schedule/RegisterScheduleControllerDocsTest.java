package com.odcloud.adapter.in.controller.schedule.register_schedule;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
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
import com.odcloud.application.port.in.RegisterSchedulerUseCase;
import com.odcloud.application.service.register_schedule.RegisterScheduleServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class RegisterScheduleControllerDocsTest extends RestDocsSupport {

    private final RegisterSchedulerUseCase useCase = mock(RegisterSchedulerUseCase.class);
    private final String apiName = "스케줄 등록 API";

    @Override
    protected Object initController() {
        return new RegisterScheduleController(useCase);
    }

    @Nested
    @DisplayName("[register] 스케줄을 등록하는 API")
    class Describe_register {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .startDt("2025-01-01 10:00:00")
                .build();
            String authorization = "error token";
            given(useCase.register(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument(request, authorization, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 유효한 정보로 개인 스케줄을 등록한다")
        void success_personalSchedule() throws Exception {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .startDt("2025-01-01 10:00:00")
                .notificationDt("2025-01-01 09:50:00")
                .build();

            RegisterScheduleServiceResponse serviceResponse =
                RegisterScheduleServiceResponse.ofSuccess();

            given(useCase.register(any())).willReturn(serviceResponse);

            // when & then
            performDocument(request, "Bearer test", status().isOk(), "success", "success",
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
        @DisplayName("[error] 스케줄 내용을 입력하지 않은 경우 400 에러를 반환한다")
        void error_contentIsBlank() throws Exception {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .startDt("2025-01-01 10:00:00")
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(), "스케줄 내용 미입력");
        }

        @Test
        @DisplayName("[error] 시작일시를 입력하지 않은 경우 400 에러를 반환한다")
        void error_startDtIsNull() throws Exception {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(), "시작일시 미입력");
        }

        @Test
        @DisplayName("[error] 유효하지 않은 시작일시 형식인 경우 400 에러를 반환한다")
        void error_invalidStartDtFormat() throws Exception {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("개인 회의")
                .startDt("2025/01/01 10:00:00")
                .build();

            // when & then
            performErrorDocument(request, "Bearer test", status().isBadRequest(),
                "유효하지 않은 시작일시 형식");
        }

        @Test
        @DisplayName("[error] 권한이 없는 그룹에 스케줄 등록 시도 시 401 에러를 반환한다")
        void error_accessDenied() throws Exception {
            // given
            RegisterScheduleRequest request = RegisterScheduleRequest.builder()
                .content("그룹 회의")
                .startDt("2025-01-01 10:00:00")
                .groupId("other-group")
                .build();

            given(useCase.register(any()))
                .willThrow(new CustomAuthenticationException(ErrorCode.ACCESS_DENIED));

            // when & then
            performErrorDocument(request, "Bearer test", status().isUnauthorized(), "그룹 접근 권한 없음");
        }
    }

    private void performDocument(
        RegisterScheduleRequest request,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        JsonFieldType contentType = request.content() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType startDtType = request.startDt() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType groupIdType = request.groupId() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;
        JsonFieldType notificationDtType = request.notificationDt() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;

        mockMvc.perform(post("/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Schedule")
                    .summary("스케줄 등록 API")
                    .description("새로운 스케줄을 등록하는 API 입니다")
                    .requestFields(
                        fieldWithPath("content").type(contentType)
                            .description("스케줄 내용"),
                        fieldWithPath("startDt").type(startDtType)
                            .description("시작일시 (yyyy-MM-dd HH:mm:ss)"),
                        fieldWithPath("groupId").type(groupIdType)
                            .description("그룹 ID (선택)"),
                        fieldWithPath("notificationDt").type(notificationDtType)
                            .description("알림일시 (선택, yyyy-MM-dd HH:mm:ss)")
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
        RegisterScheduleRequest request,
        String authorization,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(request, authorization, status, identifier, "error",
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
