package com.odcloud.application.group.port.in.command;

import lombok.Builder;

@Builder
public record DeleteGroupCommand(
    Long groupId,
    String currentOwnerEmail
) {

}
