package com.odcloud.adapter.in.controller.update_folder;

import com.odcloud.application.port.in.command.UpdateFolderCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.StringUtil;
import lombok.Builder;

@Builder
record UpdateFolderRequest(

    String name,

    Long parentId
) {

    UpdateFolderCommand toCommand(Long folderId, Account account) {
        return UpdateFolderCommand.builder()
            .folderId(folderId)
            .account(account)
            .name(name)
            .parentId(parentId)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
