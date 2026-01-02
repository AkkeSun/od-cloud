package com.odcloud.application.group.port.in.command;

import lombok.Builder;

@Builder
public record DeleteGroupCommand(
    String groupId,
    String currentOwnerEmail
) {

}
