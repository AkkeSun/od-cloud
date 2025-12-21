package com.odcloud.adapter.in.controller.find_question;

import com.odcloud.application.port.in.FindQuestionUseCase;
import com.odcloud.application.service.find_question.FindQuestionServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FindQuestionController {

    private final FindQuestionUseCase useCase;

    @GetMapping("/questions/{questionId}")
    ApiResponse<FindQuestionResponse> findQuestion(@PathVariable Long questionId) {
        FindQuestionServiceResponse response = useCase.findQuestion(questionId);
        return ApiResponse.ok(FindQuestionResponse.of(response));
    }
}
