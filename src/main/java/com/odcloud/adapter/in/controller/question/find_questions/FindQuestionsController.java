package com.odcloud.adapter.in.controller.question.find_questions;

import com.odcloud.application.question.port.in.FindQuestionsUseCase;
import com.odcloud.application.question.service.find_questions.FindQuestionsResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FindQuestionsController {

    private final FindQuestionsUseCase useCase;

    @GetMapping("/questions")
    ApiResponse<FindQuestionsResponse> findQuestions(@ModelAttribute FindQuestionsRequest request) {
        return ApiResponse.ok(useCase.findQuestions(request.toCommand()));
    }
}
