package com.odcloud.adapter.in.controller.group.update_group;

import com.odcloud.application.group.port.in.command.UpdateGroupCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UpdateGroupRequest {

    private String ownerEmail;
    private String name;

    UpdateGroupCommand toCommand(String groupId, Account account) {
        return UpdateGroupCommand.builder()
            .groupId(groupId)
            .currentOwnerEmail(account.getEmail())
            .newOwnerEmail(ownerEmail)
            .name(name)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
