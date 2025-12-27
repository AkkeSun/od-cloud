package com.odcloud.application.port.in.command;

import lombok.Builder;

@Builder
public record UpdateGroupAccountStatusCommand(
    String groupId,
    Long accountId,
    String groupOwnerEmail,
    String status,
    String memo
) {

}
