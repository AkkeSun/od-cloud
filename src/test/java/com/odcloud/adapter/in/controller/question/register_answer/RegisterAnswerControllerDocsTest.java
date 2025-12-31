package com.odcloud.adapter.in.controller.question.register_answer;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odcloud.RestDocsSupport;
import com.odcloud.application.question.port.in.RegisterAnswerUseCase;
import com.odcloud.application.question.service.register_answer.RegisterAnswerServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class RegisterAnswerControllerDocsTest extends RestDocsSupport {

    private final RegisterAnswerUseCase useCase = mock(RegisterAnswerUseCase.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected Object initController() {
        return new RegisterAnswerController(useCase);
    }

    @Test
    @DisplayName("[success] 문의 게시글 답변을 등록한다")
    void success() throws Exception {
        // given
        RegisterAnswerRequest request = RegisterAnswerRequest.builder()
            .content("API 사용 방법은 다음과 같습니다...")
            .build();

        RegisterAnswerServiceResponse serviceResponse = RegisterAnswerServiceResponse.ofSuccess();
        given(useCase.registerAnswer(any())).willReturn(serviceResponse);

        // when & then
        mockMvc.perform(post("/questions/{questionId}/answers", 1L)
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("문의 게시글 답변 등록 API",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Question")
                    .description("문의 게시글 답변을 등록합니다. 답변 등록 시 문의자에게 푸시 알림이 전송됩니다.")
                    .requestHeaders(
                        headerWithName("Authorization").description("인증 토큰")
                    )
                    .pathParameters(
                        parameterWithName("questionId").description("문의 게시글 ID")
                    )
                    .requestSchema(Schema.schema("RegisterAnswerRequest"))
                    .requestFields(
                        fieldWithPath("content").type(JsonFieldType.STRING)
                            .description("답변 내용")
                    )
                    .responseSchema(Schema.schema("RegisterAnswerResponse"))
                    .responseFields(
                        fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                            .description("상태 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("상태 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("응답 데이터"),
                        fieldWithPath("data.result").type(JsonFieldType.BOOLEAN)
                            .description("등록 성공 여부")
                    )
                    .build()
                )
            ));
    }

    @Test
    @DisplayName("[error] 이미 답변이 등록된 문의에 답변을 등록하려고 하면 500 에러를 반환한다")
    void error_alreadyAnswered() throws Exception {
        // given
        RegisterAnswerRequest request = RegisterAnswerRequest.builder()
            .content("API 사용 방법은 다음과 같습니다...")
            .build();

        given(useCase.registerAnswer(any()))
            .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                com.odcloud.infrastructure.exception.ErrorCode.Business_ALREADY_EXISTS_ANSWER));

        // when & then
        mockMvc.perform(post("/questions/{questionId}/answers", 1L)
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("[error] 존재하지 않는 문의에 답변을 등록하려고 하면 500 에러를 반환한다")
    void error_questionNotFound() throws Exception {
        // given
        RegisterAnswerRequest request = RegisterAnswerRequest.builder()
            .content("API 사용 방법은 다음과 같습니다...")
            .build();

        given(useCase.registerAnswer(any()))
            .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_QUESTION));

        // when & then
        mockMvc.perform(post("/questions/{questionId}/answers", 999L)
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isInternalServerError());
    }
}
