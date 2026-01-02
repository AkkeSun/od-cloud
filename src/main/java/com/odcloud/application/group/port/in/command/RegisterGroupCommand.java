package com.odcloud.application.group.port.in.command;

import lombok.Builder;

@Builder
public record RegisterGroupCommand(
    String name,
    String ownerEmail
) {

}
