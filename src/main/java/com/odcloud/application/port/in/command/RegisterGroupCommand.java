package com.odcloud.application.port.in.command;

import lombok.Builder;

@Builder
public record RegisterGroupCommand(
    String id,
    String ownerEmail,
    String description
) {

}
