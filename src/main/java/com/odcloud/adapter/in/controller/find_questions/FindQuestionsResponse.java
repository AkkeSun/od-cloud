package com.odcloud.adapter.in.controller.find_questions;

import com.odcloud.application.service.find_questions.FindQuestionsServiceResponse;
import com.odcloud.domain.model.Question;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record FindQuestionsResponse(
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    List<QuestionDto> questions
) {

    public static FindQuestionsResponse of(FindQuestionsServiceResponse response) {
        return FindQuestionsResponse.builder()
            .pageNumber(response.pageNumber())
            .pageSize(response.pageSize())
            .totalElements(response.totalElements())
            .totalPages(response.totalPages())
            .questions(response.questions().stream()
                .map(QuestionDto::of)
                .toList())
            .build();
    }

    @Builder
    public record QuestionDto(
        Long id,
        String writerNickname,
        String title,
        Boolean answered,
        LocalDateTime regDt
    ) {

        public static QuestionDto of(Question question) {
            return QuestionDto.builder()
                .id(question.getId())
                .writerNickname(question.getWriterNickname())
                .title(question.getTitle())
                .answered(question.getAnswered())
                .regDt(question.getRegDt())
                .build();
        }
    }
}

