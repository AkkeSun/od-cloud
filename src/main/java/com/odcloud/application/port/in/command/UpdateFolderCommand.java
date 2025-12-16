package com.odcloud.application.port.in.command;

import com.odcloud.domain.model.Account;
import lombok.Builder;

@Builder
public record UpdateFolderCommand(

    Long folderId,

    Account account,

    String name,

    Long parentId
) {

}
