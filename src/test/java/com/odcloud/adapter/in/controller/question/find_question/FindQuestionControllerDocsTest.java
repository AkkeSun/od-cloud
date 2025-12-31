package com.odcloud.adapter.in.controller.question.find_question;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
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
import com.odcloud.application.question.port.in.FindQuestionUseCase;
import com.odcloud.application.question.service.find_question.FindQuestionServiceResponse;
import com.odcloud.domain.model.Answer;
import com.odcloud.domain.model.Question;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

class FindQuestionControllerDocsTest extends RestDocsSupport {

    private final FindQuestionUseCase useCase = mock(FindQuestionUseCase.class);

    @Override
    protected Object initController() {
        return new FindQuestionController(useCase);
    }

    @Test
    @DisplayName("[success] 문의 게시글 상세 정보를 조회한다 - 답변이 있는 경우")
    void success_withAnswer() throws Exception {
        // given
        Question question = Question.builder()
            .id(1L)
            .writerEmail("user@test.com")
            .writerNickname("테스터")
            .title("API 사용 문의")
            .content("API 사용 방법을 알고 싶습니다.")
            .answered(true)
            .regDt(LocalDateTime.of(2025, 1, 20, 10, 0))
            .build();

        Answer answer = Answer.builder()
            .id(1L)
            .questionId(1L)
            .writerEmail("admin@test.com")
            .writerNickname("관리자")
            .content("API 사용 방법은 다음과 같습니다...")
            .regDt(LocalDateTime.of(2025, 1, 20, 15, 30))
            .build();

        FindQuestionServiceResponse serviceResponse = FindQuestionServiceResponse.of(question,
            answer);
        given(useCase.findQuestion(any())).willReturn(serviceResponse);

        // when & then
        mockMvc.perform(get("/questions/{questionId}", 1L))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("문의 게시글 상세 조회 API",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Question")
                    .description("문의 게시글의 상세 정보를 조회합니다. 답변이 있는 경우 함께 조회됩니다.")
                    .pathParameters(
                        parameterWithName("questionId").description("문의 게시글 ID")
                    )
                    .responseSchema(Schema.schema("FindQuestionResponse"))
                    .responseFields(
                        fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                            .description("상태 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("상태 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("응답 데이터"),
                        fieldWithPath("data.question").type(JsonFieldType.OBJECT)
                            .description("문의 정보"),
                        fieldWithPath("data.question.id").type(JsonFieldType.NUMBER)
                            .description("문의 ID"),
                        fieldWithPath("data.question.writerNickname").type(JsonFieldType.STRING)
                            .description("작성자 닉네임"),
                        fieldWithPath("data.question.title").type(JsonFieldType.STRING)
                            .description("제목"),
                        fieldWithPath("data.question.content").type(JsonFieldType.STRING)
                            .description("내용"),
                        fieldWithPath("data.question.answered").type(JsonFieldType.BOOLEAN)
                            .description("답변 완료 여부"),
                        fieldWithPath("data.question.regDt").type(JsonFieldType.STRING)
                            .description("등록일시"),
                        fieldWithPath("data.answer").type(JsonFieldType.OBJECT)
                            .description("답변 정보 (답변이 없는 경우 null)"),
                        fieldWithPath("data.answer.id").type(JsonFieldType.NUMBER)
                            .description("답변 ID"),
                        fieldWithPath("data.answer.writerNickname").type(JsonFieldType.STRING)
                            .description("답변 작성자 닉네임"),
                        fieldWithPath("data.answer.content").type(JsonFieldType.STRING)
                            .description("답변 내용"),
                        fieldWithPath("data.answer.regDt").type(JsonFieldType.STRING)
                            .description("답변 등록일시")
                    )
                    .build()
                )
            ));
    }

    @Test
    @DisplayName("[success] 문의 게시글 상세 정보를 조회한다 - 답변이 없는 경우")
    void success_withoutAnswer() throws Exception {
        // given
        Question question = Question.builder()
            .id(1L)
            .writerEmail("user@test.com")
            .writerNickname("테스터")
            .title("API 사용 문의")
            .content("API 사용 방법을 알고 싶습니다.")
            .answered(false)
            .regDt(LocalDateTime.of(2025, 1, 20, 10, 0))
            .build();

        FindQuestionServiceResponse serviceResponse = FindQuestionServiceResponse.of(question,
            null);
        given(useCase.findQuestion(any())).willReturn(serviceResponse);

        // when & then
        mockMvc.perform(get("/questions/{questionId}", 1L))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[error] 존재하지 않는 문의 게시글을 조회하려고 하면 500 에러를 반환한다")
    void error_questionNotFound() throws Exception {
        // given
        given(useCase.findQuestion(any()))
            .willThrow(new com.odcloud.infrastructure.exception.CustomBusinessException(
                com.odcloud.infrastructure.exception.ErrorCode.Business_NOT_FOUND_QUESTION));

        // when & then
        mockMvc.perform(get("/questions/{questionId}", 999L))
            .andDo(print())
            .andExpect(status().isInternalServerError());
    }
}
