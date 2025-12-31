package com.odcloud.adapter.in.controller.schedule.delete_schedule;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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
import com.odcloud.application.schedule.port.in.DeleteScheduleUseCase;
import com.odcloud.application.schedule.service.delete_schedule.DeleteScheduleServiceResponse;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;

class DeleteScheduleControllerDocsTest extends RestDocsSupport {

    private final DeleteScheduleUseCase useCase = mock(DeleteScheduleUseCase.class);
    private final String apiName = "스케줄 삭제 API";

    @Override
    protected Object initController() {
        return new DeleteScheduleController(useCase);
    }

    @Nested
    @DisplayName("[delete] 스케줄을 삭제하는 API")
    class Describe_delete {

        @Test
        @DisplayName("[error] 권한 정보가 없는 사용자가 API 를 호출한 경우 401 코드와 에러 메시지를 응답한다.")
        void error_unauthorized() throws Exception {
            // given
            Long scheduleId = 1L;
            String authorization = "error token";
            given(useCase.delete(any(), any())).willThrow(
                new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN_BY_SECURITY));

            // when then
            performErrorDocument(scheduleId, authorization, status().isUnauthorized(),
                "인증 토큰 미입력 혹은 만료된 토큰 입력");
        }

        @Test
        @DisplayName("[success] 유효한 정보로 개인 스케줄을 삭제한다")
        void success_deletePersonalSchedule() throws Exception {
            // given
            Long scheduleId = 1L;

            DeleteScheduleServiceResponse serviceResponse =
                DeleteScheduleServiceResponse.ofSuccess();

            given(useCase.delete(any(), any())).willReturn(serviceResponse);

            // when & then
            performDocument(scheduleId, "Bearer test", status().isOk(), "success", "success",
                fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                    .description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("상태 메시지"),
                fieldWithPath("data").type(JsonFieldType.OBJECT)
                    .description("응답 데이터"),
                fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                    .description("삭제 성공 여부")
            );
        }

        @Test
        @DisplayName("[error] 존재하지 않는 스케줄 삭제 시 500 에러를 반환한다")
        void error_notFoundSchedule() throws Exception {
            // given
            Long scheduleId = 999L;

            given(useCase.delete(any(), any()))
                .willThrow(new CustomBusinessException(ErrorCode.Business_NOT_FOUND_SCHEDULE));

            // when & then
            performErrorDocument(scheduleId, "Bearer test", status().isInternalServerError(),
                "존재하지 않는 스케줄");
        }

        @Test
        @DisplayName("[error] 권한이 없는 스케줄 삭제 시도 시 500 에러를 반환한다")
        void error_notScheduleOwner() throws Exception {
            // given
            Long scheduleId = 1L;

            given(useCase.delete(any(), any()))
                .willThrow(new CustomAuthorizationException(ErrorCode.ACCESS_DENIED));

            // when & then
            performErrorDocument(scheduleId, "Bearer test", status().isForbidden(),
                "스케줄 삭제 권한 없음");
        }
    }

    private void performDocument(
        Long scheduleId,
        String authorization,
        ResultMatcher status,
        String docIdentifier,
        String responseSchema,
        FieldDescriptor... responseFields
    ) throws Exception {

        mockMvc.perform(delete("/schedules/{scheduleId}", scheduleId)
                .header("Authorization", authorization))
            .andDo(print())
            .andExpect(status)
            .andDo(document(String.format("[%s] %s", apiName, docIdentifier),
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Schedule")
                    .summary("스케줄 삭제 API")
                    .description("스케줄을 삭제하는 API 입니다. 개인 스케줄은 작성자만, 그룹 스케줄은 그룹 멤버만 삭제할 수 있습니다.")
                    .pathParameters(
                        parameterWithName("scheduleId").description("스케줄 ID")
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
        String authorization,
        ResultMatcher status,
        String identifier
    ) throws Exception {
        performDocument(scheduleId, authorization, status, identifier, "error",
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
