package com.odcloud.application.question.service.find_questions;

import com.odcloud.domain.model.Question;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record FindQuestionsServiceResponse(
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    List<Question> questions
) {

    public static FindQuestionsServiceResponse of(Page<Question> page) {
        return FindQuestionsServiceResponse.builder()
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .questions(page.getContent())
            .build();
    }
}
