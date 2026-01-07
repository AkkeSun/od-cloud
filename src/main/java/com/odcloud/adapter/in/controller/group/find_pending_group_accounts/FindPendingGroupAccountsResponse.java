package com.odcloud.adapter.in.controller.group.find_pending_group_accounts;

import com.odcloud.application.group.service.find_pending_group_accounts.FindPendingGroupAccountsServiceResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

record FindPendingGroupAccountsResponse(
    List<GroupPendingAccounts> groups
) {

    static FindPendingGroupAccountsResponse of(
        FindPendingGroupAccountsServiceResponse serviceResponse
    ) {
        List<GroupPendingAccounts> groups = serviceResponse.groups().stream()
            .map(group -> new GroupPendingAccounts(
                group.groupId(),
                group.groupName(),
                group.pendingAccounts().stream()
                    .map(account -> new PendingAccountInfo(
                        account.accountId(),
                        account.nickname(),
                        account.name(),
                        account.email(),
                        account.requestDate()
                    ))
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());

        return new FindPendingGroupAccountsResponse(groups);
    }

    record GroupPendingAccounts(
        Long groupId,
        String groupName,
        List<PendingAccountInfo> pendingAccounts
    ) {

    }

    record PendingAccountInfo(
        Long accountId,
        String nickname,
        String name,
        String email,
        LocalDateTime requestDate
    ) {

    }
}
