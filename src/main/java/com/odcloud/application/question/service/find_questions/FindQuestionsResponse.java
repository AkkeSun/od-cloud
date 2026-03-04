package com.odcloud.application.question.service.find_questions;

import com.odcloud.domain.model.Question;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record FindQuestionsResponse(
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    List<Question> questions
) {

    public static FindQuestionsResponse of(Page<Question> page) {
        return FindQuestionsResponse.builder()
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .questions(page.getContent())
            .build();
    }
}
