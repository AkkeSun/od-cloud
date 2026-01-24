package com.odcloud.application.file.port.in.command;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record UpdateFolderCommand(

    Long folderId,

    Account account,

    String name,

    Long parentId
) {

    public boolean isFolderLocChange(Long parentId) {
        return this.parentId != null && !this.parentId.equals(parentId);
    }

    public boolean isFolderNameChange(String folderName) {
        return this.name != null && !this.name.equals(folderName);
    }
}
