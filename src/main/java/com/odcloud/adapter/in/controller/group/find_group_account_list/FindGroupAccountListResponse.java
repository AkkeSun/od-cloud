package com.odcloud.adapter.in.controller.group.find_group_account_list;

import com.odcloud.application.service.find_group_account_list.FindGroupAccountListServiceResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
record FindGroupAccountListResponse(
    List<GroupAccountInfo> groupAccounts
) {

    static FindGroupAccountListResponse of(FindGroupAccountListServiceResponse response) {
        List<GroupAccountInfo> groupAccountInfos = response.groupAccounts().stream()
            .map(ga -> GroupAccountInfo.builder()
                .id(ga.getId())
                .groupId(ga.getGroupId())
                .accountId(ga.getAccountId())
                .name(ga.getName())
                .nickName(ga.getNickName())
                .email(ga.getEmail())
                .status(ga.getStatus())
                .updateDt(ga.getModDt())
                .regDt(ga.getRegDt())
                .build())
            .toList();

        return FindGroupAccountListResponse.builder()
            .groupAccounts(groupAccountInfos)
            .build();
    }

    @Builder
    record GroupAccountInfo(
        Long id,
        String groupId,
        Long accountId,
        String name,
        String nickName,
        String email,
        String status,
        LocalDateTime updateDt,
        LocalDateTime regDt
    ) {

    }
}
