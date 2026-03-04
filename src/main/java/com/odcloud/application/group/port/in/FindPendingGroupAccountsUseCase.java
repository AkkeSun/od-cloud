package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.find_pending_group_accounts.FindPendingGroupAccountsResponse;
import com.odcloud.domain.model.Account;

public interface FindPendingGroupAccountsUseCase {

    FindPendingGroupAccountsResponse findPendingAccounts(Account account);
}
