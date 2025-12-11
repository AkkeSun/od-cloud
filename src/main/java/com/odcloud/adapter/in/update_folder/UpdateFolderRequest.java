package com.odcloud.adapter.in.update_folder;

import com.odcloud.application.port.in.command.UpdateFolderCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.StringUtil;
import com.odcloud.infrastructure.validation.Contains;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.CustomGroups;
import lombok.Builder;

@Builder
record UpdateFolderRequest(

    String name,

    Long parentId,

    @Contains(values = {"PRIVATE", "PUBLIC"}, groups = CustomGroups.class)
    String accessLevel
) {

    UpdateFolderCommand toCommand(Long folderId, Account account) {
        return UpdateFolderCommand.builder()
            .folderId(folderId)
            .account(account)
            .name(name)
            .parentId(parentId)
            .accessLevel(accessLevel)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
