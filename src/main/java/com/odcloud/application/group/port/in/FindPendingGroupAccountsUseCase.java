package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.find_pending_group_accounts.FindPendingGroupAccountsServiceResponse;
import com.odcloud.domain.model.Account;

public interface FindPendingGroupAccountsUseCase {

    FindPendingGroupAccountsServiceResponse findPendingAccounts(Account account);
}
