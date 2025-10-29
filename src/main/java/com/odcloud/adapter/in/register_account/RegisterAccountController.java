package com.odcloud.adapter.in.register_account;

import com.odcloud.application.port.in.RegisterAccountUseCase;
import com.odcloud.application.service.register_account.RegisterAccountServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.infrastructure.validation.groups.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RegisterAccountController {

    private final RegisterAccountUseCase useCase;

    @PostMapping("/accounts")
    ApiResponse<RegisterAccountResponse> registerAccount(
        @RequestBody @Validated(ValidationSequence.class) RegisterAccountRequest request) {
        RegisterAccountServiceResponse serviceResponse = useCase.register(request.toCommand());

        return ApiResponse.ok(RegisterAccountResponse.of(serviceResponse));
    }
}
