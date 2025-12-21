package com.odcloud.adapter.in.controller.find_questions;

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
import com.odcloud.application.port.in.FindQuestionsUseCase;
import com.odcloud.application.service.find_questions.FindQuestionsServiceResponse;
import com.odcloud.domain.model.Question;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.restdocs.payload.JsonFieldType;

class FindQuestionsControllerDocsTest extends RestDocsSupport {

    private final FindQuestionsUseCase useCase = mock(FindQuestionsUseCase.class);

    @Override
    protected Object initController() {
        return new FindQuestionsController(useCase);
    }

    @Test
    @DisplayName("[success] 문의 게시글 목록을 조회한다")
    void success() throws Exception {
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

        FindQuestionsServiceResponse serviceResponse = FindQuestionsServiceResponse.of(
            new PageImpl<>(List.of(question), PageRequest.of(0, 10), 1));
        given(useCase.findQuestions(any())).willReturn(serviceResponse);

        // when & then
        mockMvc.perform(get("/questions")
                .param("page", "0")
                .param("size", "10"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("문의 게시글 목록 조회 API",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                    .tag("Question")
                    .description("문의 게시글 목록을 페이징하여 조회합니다")
                    .queryParameters(
                        parameterWithName("page").description("페이지 번호 (기본값: 0)").optional(),
                        parameterWithName("size").description("페이지 크기 (기본값: 10)").optional()
                    )
                    .responseSchema(Schema.schema("FindQuestionsResponse"))
                    .responseFields(
                        fieldWithPath("httpStatus").type(JsonFieldType.NUMBER)
                            .description("상태 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("상태 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("응답 데이터"),
                        fieldWithPath("data.pageNumber").type(JsonFieldType.NUMBER)
                            .description("현재 페이지 번호"),
                        fieldWithPath("data.pageSize").type(JsonFieldType.NUMBER)
                            .description("페이지 크기"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER)
                            .description("전체 데이터 개수"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                            .description("전체 페이지 수"),
                        fieldWithPath("data.questions").type(JsonFieldType.ARRAY)
                            .description("문의 게시글 목록"),
                        fieldWithPath("data.questions[].id").type(JsonFieldType.NUMBER)
                            .description("문의 ID"),
                        fieldWithPath("data.questions[].writerNickname").type(JsonFieldType.STRING)
                            .description("작성자 닉네임"),
                        fieldWithPath("data.questions[].title").type(JsonFieldType.STRING)
                            .description("제목"),
                        fieldWithPath("data.questions[].answered").type(JsonFieldType.BOOLEAN)
                            .description("답변 완료 여부"),
                        fieldWithPath("data.questions[].regDt").type(JsonFieldType.STRING)
                            .description("등록일시")
                    )
                    .build()
                )
            ));
    }
}
