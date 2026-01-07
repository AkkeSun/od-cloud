package com.odcloud.application.file.port.in.command;

import lombok.Builder;

@Builder
public record RegisterFolderCommand(
    Long parentId,
    Long groupId,
    String name,
    String owner
) {

}
