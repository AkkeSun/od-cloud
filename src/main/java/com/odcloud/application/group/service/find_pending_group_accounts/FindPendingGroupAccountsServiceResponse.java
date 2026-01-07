package com.odcloud.application.group.service.find_pending_group_accounts;

import com.odcloud.domain.model.GroupAccount;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

public record FindPendingGroupAccountsServiceResponse(
    List<GroupPendingAccounts> groups
) {

    public static FindPendingGroupAccountsServiceResponse of(
        List<GroupAccount> groupAccounts
    ) {
        Map<Long, List<GroupAccount>> accountsByGroup = groupAccounts.stream()
            .collect(Collectors.groupingBy(GroupAccount::getGroupId));

        List<GroupPendingAccounts> groupPendingAccountsList = accountsByGroup.entrySet()
            .stream()
            .map(entry -> {
                Long groupId = entry.getKey();
                List<GroupAccount> pendingAccounts = entry.getValue();
                String groupName = pendingAccounts.getFirst().getGroupName();

                return GroupPendingAccounts.builder()
                    .groupId(groupId)
                    .groupName(groupName)
                    .pendingAccounts(
                        pendingAccounts.stream()
                            .map(PendingAccountInfo::from)
                            .collect(Collectors.toList())
                    )
                    .build();
            })
            .collect(Collectors.toList());

        return new FindPendingGroupAccountsServiceResponse(groupPendingAccountsList);
    }

    @Builder
    public record GroupPendingAccounts(
        Long groupId,
        String groupName,
        List<PendingAccountInfo> pendingAccounts
    ) {

    }

    @Builder
    public record PendingAccountInfo(
        Long accountId,
        String nickname,
        String name,
        String email,
        LocalDateTime requestDate
    ) {

        public static PendingAccountInfo from(GroupAccount groupAccount) {
            return PendingAccountInfo.builder()
                .accountId(groupAccount.getAccountId())
                .nickname(groupAccount.getNickName())
                .name(groupAccount.getName())
                .email(groupAccount.getEmail())
                .requestDate(groupAccount.getRegDt())
                .build();
        }
    }
}
