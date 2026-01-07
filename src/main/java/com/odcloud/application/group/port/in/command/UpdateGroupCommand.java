package com.odcloud.application.group.port.in.command;

import lombok.Builder;

@Builder
public record UpdateGroupCommand(
    Long groupId,
    String currentOwnerEmail,
    String newOwnerEmail,
    String name
) {

}
