package com.odcloud.adapter.in.controller.register_question;

import com.odcloud.application.port.in.RegisterQuestionUseCase;
import com.odcloud.application.service.register_question.RegisterQuestionServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RegisterQuestionController {

    private final RegisterQuestionUseCase useCase;

    @PostMapping("/questions")
    ApiResponse<RegisterQuestionResponse> registerQuestion(
        @Valid @RequestBody RegisterQuestionRequest request,
        @LoginAccount Account account
    ) {
        RegisterQuestionServiceResponse response = useCase.registerQuestion(
            request.toCommand(account));
        return ApiResponse.ok(RegisterQuestionResponse.of(response));
    }
}
