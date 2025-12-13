package com.odcloud.adapter.in.find_schedules;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
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
import com.odcloud.application.port.in.FindSchedulesUseCase;
import com.odcloud.application.service.find_schedules.FindSchedulesServiceResponse;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class FindSchedulesControllerDocsTest extends RestDocsSupport {

    private final FindSchedulesUseCase useCase = mock(FindSchedulesUseCase.class);
    private final String apiName = "스케줄 조회 API";

    @Override
    protected Object initController() {
        return new FindSchedulesController(useCase);
    }

    @Nested
    @DisplayName("[findSchedules] 스케줄을 조회하는 API")
    class Describe_findSchedules {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            String authorization = "error token";
            given(useCase.findSchedules(any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument("2025-01-15", "PRIVATE", authorization,
                status().isUnauthorized(), "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 월별 일정을 조회한다")
        void success_getMonthlyAllSchedules() throws Exception {
            // given
            Schedule schedule1 = createSchedule(1L, "user@example.com", null, "개인 회의",
                LocalDateTime.of(2025, 1, 5, 10, 0));
            Schedule schedule2 = createSchedule(2L, "owner@example.com", "group-1", "그룹 회의",
                LocalDateTime.of(2025, 1, 15, 14, 0));

            FindSchedulesServiceResponse serviceResponse = FindSchedulesServiceResponse.of(
                Arrays.asList(schedule1, schedule2));

            given(useCase.findSchedules(any())).willReturn(serviceResponse);

            // when & then
            performDocument("2025-01-15", null, "Bearer test",
                status().isOk(), "success_월별_전체일정", "success");
        }

        @Test
        @DisplayName("[error] 유효하지 않은 날짜 형식인 경우 400 에러를 반환한다")
        void error_invalidDateFormat() throws Exception {
            // when & then
            performErrorDocument("2025/01/15", "PRIVATE", "Bearer test",
                status().isBadRequest(), "유효하지 않은 날짜 형식");
        }
    }

    private Schedule createSchedule(Long id, String email, String groupId, String content,
        LocalDateTime startDt) {
        return Schedule.builder()
            .id(id)
            .writerEmail(email)
            .groupId(groupId)
            .content(content)
            .startDt(startDt)
            .notificationDt(LocalDateTime.now())
            .notificationYn("N")
            .modDt(null)
            .regDt(LocalDateTime.now())
            .build();
    }

    private void performDocument(
        String baseDate,
        String filterType,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema
    ) throws Exception {

        mockMvc.perform(get("/schedules")
                .param("baseDate", baseDate)
                .param("filterType", filterType)
                .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Schedule")
                    .summary("스케줄 조회 API")
                    .description(
                        "월별 일정을 조회하는 API입니다. filterType으로 개인/그룹 일정을 필터링할 수 있습니다.")
                    .queryParameters(
                        parameterWithName("baseDate").description("기준일 (yyyy-MM-dd) : 미입력 시 현재 날짜").optional(),
                        parameterWithName("filterType").description(
                            "필터 타입 (PRIVATE: 개인일정, 그룹명: 해당 그룹 일정, null/빈값: 전체 일정)").optional()
                    )
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .responseFields(
                        fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                            .description("상태 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("상태 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("응답 데이터"),
                        fieldWithPath("data.schedules").type(JsonFieldType.ARRAY)
                            .description("일정 목록"),
                        fieldWithPath("data.schedules[].id").type(JsonFieldType.NUMBER)
                            .description("일정 ID"),
                        fieldWithPath("data.schedules[].writerEmail").type(JsonFieldType.STRING)
                            .description("작성자 이메일"),
                        fieldWithPath("data.schedules[].groupId").type(JsonFieldType.STRING)
                            .description("그룹 ID (개인일정인 경우 null)").optional(),
                        fieldWithPath("data.schedules[].content").type(JsonFieldType.STRING)
                            .description("일정 내용"),
                        fieldWithPath("data.schedules[].startDt").type(JsonFieldType.STRING)
                            .description("시작일시"),
                        fieldWithPath("data.schedules[].notificationDt").type(JsonFieldType.STRING)
                            .description("알림일시")
                    )
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] " + responseSchema))
                    .build()
                )
            ));
    }

    private void performErrorDocument(
        String baseDate,
        String filterType,
        String authorization,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        mockMvc.perform(get("/schedules")
                .param("baseDate", baseDate)
                .param("filterType", filterType)
                .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, identifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Schedule")
                    .summary("스케줄 조회 API")
                    .description("월별 일정을 조회하는 API입니다")
                    .queryParameters(
                        parameterWithName("baseDate").description("기준일").optional(),
                        parameterWithName("filterType").description("필터 타입").optional()
                    )
                    .requestHeaders(headerWithName("Authorization").description("인증 토큰"))
                    .responseFields(
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
                    )
                    .requestSchema(Schema.schema("[request] " + apiName))
                    .responseSchema(Schema.schema("[response] error"))
                    .build()
                )
            ));
    }
}
