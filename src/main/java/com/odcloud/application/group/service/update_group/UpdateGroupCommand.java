package com.odcloud.application.group.service.update_group;

import lombok.Builder;

@Builder
public record UpdateGroupCommand(
    Long groupId,
    String currentOwnerEmail,
    String newOwnerEmail,
    String name
) {

}
