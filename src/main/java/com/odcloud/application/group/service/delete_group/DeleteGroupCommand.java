package com.odcloud.application.group.service.delete_group;

import lombok.Builder;

@Builder
public record DeleteGroupCommand(
    Long groupId,
    String currentOwnerEmail
) {
}
