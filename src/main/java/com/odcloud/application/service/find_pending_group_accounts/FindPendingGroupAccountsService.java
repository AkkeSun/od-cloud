package com.odcloud.application.service.find_pending_group_accounts;

import com.odcloud.application.port.in.FindPendingGroupAccountsUseCase;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.GroupAccount;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class FindPendingGroupAccountsService implements FindPendingGroupAccountsUseCase {

    private final GroupStoragePort groupStoragePort;

    @Override
    public FindPendingGroupAccountsServiceResponse findPendingAccounts(Account account) {
        List<GroupAccount> pendingAccounts =
            groupStoragePort.findPendingGroupAccountsByOwnerEmail(account.getEmail());
        return FindPendingGroupAccountsServiceResponse.of(pendingAccounts);
    }
}
