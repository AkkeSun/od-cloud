package com.odcloud.adapter.in.controller.account.delete_account;

import com.odcloud.application.account.port.in.DeleteAccountUseCase;
import com.odcloud.application.account.service.delete_account.DeleteAccountServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class DeleteAccountController {

    private final DeleteAccountUseCase deleteAccountUseCase;

    @DeleteMapping("/accounts")
    ApiResponse<DeleteAccountResponse> delete(@LoginAccount Account account) {
        DeleteAccountServiceResponse response = deleteAccountUseCase.delete(account);
        return ApiResponse.ok(DeleteAccountResponse.of(response));
    }
}
