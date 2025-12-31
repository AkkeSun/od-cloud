package com.odcloud.adapter.in.controller.group.find_pending_group_accounts;

import com.odcloud.application.group.port.in.FindPendingGroupAccountsUseCase;
import com.odcloud.application.group.service.find_pending_group_accounts.FindPendingGroupAccountsServiceResponse;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.resolver.LoginAccount;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FindPendingGroupAccountsController {

    private final FindPendingGroupAccountsUseCase useCase;

    @GetMapping("/groups/pending-accounts")
    ApiResponse<FindPendingGroupAccountsResponse> findPendingAccounts(
        @LoginAccount Account account
    ) {
        FindPendingGroupAccountsServiceResponse serviceResponse =
            useCase.findPendingAccounts(account);
        return ApiResponse.ok(FindPendingGroupAccountsResponse.of(serviceResponse));
    }
}
