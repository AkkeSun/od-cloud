package com.odcloud.application.file.service.register_folder;

import lombok.Builder;

@Builder
public record RegisterFolderCommand(
    Long parentId,
    Long groupId,
    String name,
    String owner
) {

}
