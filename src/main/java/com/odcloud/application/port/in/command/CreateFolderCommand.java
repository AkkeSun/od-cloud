package com.odcloud.application.port.in.command;

import lombok.Builder;

@Builder
public record CreateFolderCommand(
    Long parentId,
    String groupId,
    String name,
    String owner
) {

}
