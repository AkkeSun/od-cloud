package com.odcloud.adapter.in.controller.find_questions;

import com.odcloud.application.port.in.FindQuestionsUseCase;
import com.odcloud.application.service.find_questions.FindQuestionsServiceResponse;
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
        FindQuestionsServiceResponse response = useCase.findQuestions(request.toCommand());
        return ApiResponse.ok(FindQuestionsResponse.of(response));
    }
}
