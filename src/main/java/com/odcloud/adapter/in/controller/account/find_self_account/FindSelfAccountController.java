package com.odcloud.adapter.in.controller.account.find_self_account;

import com.odcloud.application.account.port.in.FindSelfAccountUseCase;
import com.odcloud.application.account.service.find_self_account.FindSelfAccountResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FindSelfAccountController {

    private final FindSelfAccountUseCase useCase;

    @GetMapping("/accounts/self")
    ApiResponse<FindSelfAccountResponse> findSelf(@LoginAccount Account account) {
        return ApiResponse.ok(useCase.findSelf(account));
    }
}
