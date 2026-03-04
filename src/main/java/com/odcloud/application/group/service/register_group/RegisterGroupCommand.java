package com.odcloud.application.group.service.register_group;

import lombok.Builder;

@Builder
public record RegisterGroupCommand(
    String name,
    String ownerEmail
) {

}
