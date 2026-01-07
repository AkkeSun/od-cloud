package com.odcloud.application.group.port.in.command;

import lombok.Builder;

@Builder
public record UpdateGroupAccountStatusCommand(
    Long groupId,
    Long accountId,
    String groupOwnerEmail,
    String status,
    String memo
) {

}
