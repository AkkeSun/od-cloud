package com.odcloud.application.service.find_group_account_list;

import com.odcloud.domain.model.GroupAccount;
import java.util.List;
import lombok.Builder;

@Builder
public record FindGroupAccountListServiceResponse(
    List<GroupAccount> groupAccounts
) {

    public static FindGroupAccountListServiceResponse of(List<GroupAccount> groupAccounts) {
        return FindGroupAccountListServiceResponse.builder()
            .groupAccounts(groupAccounts)
            .build();
    }
}
