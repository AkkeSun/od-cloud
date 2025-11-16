package com.odcloud.adapter.in.update_group_account_status;

import com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.ToStringUtil;
import com.odcloud.infrastructure.validation.Contains;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.CustomGroups;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.NotBlankGroups;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record UpdateGroupAccountStatusRequest(

    @Contains(
        values = {"PENDING", "ACTIVE", "BLOCK"},
        message = "유효하지 않은 상태값 입니다",
        groups = CustomGroups.class
    )
    @NotBlank(message = "상태는 필수값 입니다", groups = NotBlankGroups.class)
    String status
) {

    UpdateGroupAccountStatusCommand toCommand(String groupId, Long accountId, Account account) {
        return UpdateGroupAccountStatusCommand.builder()
            .groupId(groupId)
            .accountId(accountId)
            .groupOwnerEmail(account.getEmail())
            .status(status)
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
