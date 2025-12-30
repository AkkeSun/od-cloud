package com.odcloud.adapter.in.controller.question.register_answer;

import com.odcloud.application.port.in.RegisterAnswerUseCase;
import com.odcloud.application.service.register_answer.RegisterAnswerServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RegisterAnswerController {

    private final RegisterAnswerUseCase useCase;

    @PostMapping("/questions/{questionId}/answers")
    ApiResponse<RegisterAnswerResponse> registerAnswer(
        @PathVariable Long questionId,
        @Valid @RequestBody RegisterAnswerRequest request,
        @LoginAccount Account account
    ) {
        RegisterAnswerServiceResponse response = useCase.registerAnswer(
            request.toCommand(questionId, account));
        return ApiResponse.ok(RegisterAnswerResponse.of(response));
    }
}
