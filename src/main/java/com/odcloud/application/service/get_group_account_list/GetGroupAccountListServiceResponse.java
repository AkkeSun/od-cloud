package com.odcloud.application.service.get_group_account_list;

import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.util.ToStringUtil;
import java.util.List;
import lombok.Builder;

@Builder
public record GetGroupAccountListServiceResponse(
    List<GroupAccount> groupAccounts
) {

    public static GetGroupAccountListServiceResponse of(List<GroupAccount> groupAccounts) {
        return GetGroupAccountListServiceResponse.builder()
            .groupAccounts(groupAccounts)
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
