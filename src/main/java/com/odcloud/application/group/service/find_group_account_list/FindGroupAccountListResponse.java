package com.odcloud.application.group.service.find_group_account_list;

import com.odcloud.domain.model.GroupAccount;
import java.util.List;
import lombok.Builder;

@Builder
public record FindGroupAccountListResponse(
    List<GroupAccount> groupAccounts
) {

    public static FindGroupAccountListResponse of(List<GroupAccount> groupAccounts) {
        return FindGroupAccountListResponse.builder()
            .groupAccounts(groupAccounts)
            .build();
    }
}
