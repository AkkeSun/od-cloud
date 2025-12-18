package com.odcloud.application.port.in;

import com.odcloud.application.service.find_pending_group_accounts.FindPendingGroupAccountsServiceResponse;
import com.odcloud.domain.model.Account;

public interface FindPendingGroupAccountsUseCase {

    FindPendingGroupAccountsServiceResponse findPendingAccounts(Account account);
}
