package com.odcloud.adapter.in.controller.schedule.update_schedule;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.port.in.UpdateScheduleUseCase;
import com.odcloud.application.service.update_schedule.UpdateScheduleServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class UpdateScheduleControllerDocsTest extends RestDocsSupport {

    private final UpdateScheduleUseCase useCase = mock(UpdateScheduleUseCase.class);
    private final String apiName = "스케줄 수정 API";

    @Override
    protected Object initController() {
        return new UpdateScheduleController(useCase);
    }

    @Nested
    @DisplayName("[update] 스케줄을 수정하는 API")
    class Describe_update {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .build();
            String authorization = "error token";
            given(useCase.update(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument(1L, request, authorization, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 유효한 정보로 개인 스케줄을 수정한다")
        void success_personalSchedule() throws Exception {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .notificationDt("2025-01-02 13:50:00")
                .build();

            UpdateScheduleServiceResponse serviceResponse =
                UpdateScheduleServiceResponse.ofSuccess();

            given(useCase.update(any())).willReturn(serviceResponse);

            // when & then
            performDocument(1L, request, "Bearer test", status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("수정 성공 여부")
            );
        }

        @Test
        @DisplayName("[success] 알림 시간 없이 개인 스케줄을 수정한다")
        void success_withoutNotification() throws Exception {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .build();

            UpdateScheduleServiceResponse serviceResponse =
                UpdateScheduleServiceResponse.ofSuccess();

            given(useCase.update(any())).willReturn(serviceResponse);

            // when & then
            performDocument(2L, request, "Bearer test", status().isOk(),
                "success_알림시간_없이_수정", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("수정 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 스케줄 내용을 입력하지 않은 경우 400 에러를 반환한다")
        void error_contentIsBlank() throws Exception {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .startDt("2025-01-02 14:00:00")
                .build();

            // when & then
            performErrorDocument(1L, request, "Bearer test", status().isBadRequest(),
                "스케줄 내용 미입력");
        }

        @Test
        @DisplayName("[error] 시작일시를 입력하지 않은 경우 400 에러를 반환한다")
        void error_startDtIsNull() throws Exception {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .build();

            // when & then
            performErrorDocument(1L, request, "Bearer test", status().isBadRequest(),
                "시작일시 미입력");
        }

        @Test
        @DisplayName("[error] 유효하지 않은 시작일시 형식인 경우 400 에러를 반환한다")
        void error_invalidStartDtFormat() throws Exception {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025/01/02 14:00:00")
                .build();

            // when & then
            performErrorDocument(1L, request, "Bearer test", status().isBadRequest(),
                "유효하지 않은 시작일시 형식");
        }

        @Test
        @DisplayName("[error] 권한이 없는 그룹의 스케줄 수정 시도 시 403 에러를 반환한다")
        void error_accessDeniedForGroup() throws Exception {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 그룹 회의")
                .startDt("2025-01-02 14:00:00")
                .build();

            given(useCase.update(any()))
                .willThrow(new CustomAuthorizationException(ErrorCode.ACCESS_DENIED));

            // when & then
            performErrorDocument(1L, request, "Bearer test", status().isForbidden(),
                "그룹 접근 권한 없음");
        }

        @Test
        @DisplayName("[error] 다른 사용자의 개인 스케줄 수정 시도 시 403 에러를 반환한다")
        void error_accessDeniedForPersonal() throws Exception {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .build();

            given(useCase.update(any()))
                .willThrow(new CustomAuthorizationException(ErrorCode.ACCESS_DENIED));

            // when & then
            performErrorDocument(1L, request, "Bearer test", status().isForbidden(),
                "다른 사용자 스케줄 수정 권한 없음");
        }

        @Test
        @DisplayName("[error] 존재하지 않는 스케줄 수정 시도 시 500 에러를 반환한다")
        void error_scheduleNotFound() throws Exception {
            // given
            UpdateScheduleRequest request = UpdateScheduleRequest.builder()
                .content("수정된 회의")
                .startDt("2025-01-02 14:00:00")
                .build();

            given(useCase.update(any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_NOT_FOUND_SCHEDULE));

            // when & then
            performErrorDocument(999L, request, "Bearer test", status().isInternalServerError(),
                "존재하지 않는 스케줄");
        }
    }

    private void performDocument(
        Long scheduleId,
        UpdateScheduleRequest request,
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
        JsonFieldType notificationDtType = request.notificationDt() == null ?
            JsonFieldType.NULL : JsonFieldType.STRING;

        mockMvc.perform(patch("/schedules/{scheduleId}", scheduleId)
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
                    .summary("스케줄 수정 API")
                    .description("기존 스케줄을 수정하는 API 입니다")
                    .pathParameters(
                        parameterWithName("scheduleId").description("수정할 스케줄 ID")
                    )
                    .requestFields(
                        fieldWithPath("content").type(contentType)
                            .description("스케줄 내용"),
                        fieldWithPath("startDt").type(startDtType)
                            .description("시작일시 (yyyy-MM-dd HH:mm:ss)"),
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
        Long scheduleId,
        UpdateScheduleRequest request,
        String authorization,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(scheduleId, request, authorization, status, identifier, "error",
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
