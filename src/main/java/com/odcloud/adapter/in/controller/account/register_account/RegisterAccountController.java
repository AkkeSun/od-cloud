package com.odcloud.adapter.in.controller.account.register_account;

import com.odcloud.application.port.in.RegisterAccountUseCase;
import com.odcloud.application.service.register_account.RegisterAccountServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RegisterAccountController {

    private final RegisterAccountUseCase useCase;

    @PostMapping("/accounts")
    ApiResponse<RegisterAccountResponse> registerAccount(
        @RequestHeader String googleAuthorization,
        @RequestBody @Valid RegisterAccountRequest request
    ) {
        RegisterAccountServiceResponse serviceResponse = useCase.register(
            request.toCommand(googleAuthorization));
        return ApiResponse.ok(RegisterAccountResponse.of(serviceResponse));
    }
}
