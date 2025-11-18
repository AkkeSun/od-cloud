package com.odcloud.adapter.in.update_account;

import com.odcloud.application.port.in.UpdateAccountUseCase;
import com.odcloud.application.service.update_account.UpdateAccountServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.resolver.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
class UpdateAccountController {

    private final UpdateAccountUseCase useCase;

    @PatchMapping("/accounts")
    ApiResponse<UpdateAccountResponse> updateAccount(
        @ModelAttribute @Valid UpdateAccountRequest request,
        @LoginAccount Account account
    ) {
        UpdateAccountServiceResponse serviceResponse = useCase.update(request.toCommand(account));
        return ApiResponse.ok(UpdateAccountResponse.of(serviceResponse));
    }
}
