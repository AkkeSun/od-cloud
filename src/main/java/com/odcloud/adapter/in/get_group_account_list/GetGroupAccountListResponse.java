package com.odcloud.adapter.in.get_group_account_list;

import com.odcloud.application.service.get_group_account_list.GetGroupAccountListServiceResponse;
import com.odcloud.infrastructure.util.ToStringUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record GetGroupAccountListResponse(
    List<GroupAccountInfo> groupAccounts
) {

    public static GetGroupAccountListResponse of(GetGroupAccountListServiceResponse response) {
        List<GroupAccountInfo> groupAccountInfos = response.groupAccounts().stream()
            .map(ga -> GroupAccountInfo.builder()
                .id(ga.getId())
                .groupId(ga.getGroupId())
                .accountId(ga.getAccountId())
                .name(ga.getName())
                .nickName(ga.getNickName())
                .email(ga.getEmail())
                .status(ga.getStatus())
                .updateDt(ga.getUpdateDt())
                .regDt(ga.getRegDt())
                .build())
            .toList();

        return GetGroupAccountListResponse.builder()
            .groupAccounts(groupAccountInfos)
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }

    @Builder
    public record GroupAccountInfo(
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

        @Override
        public String toString() {
            return ToStringUtil.toString(this);
        }
    }
}
