package com.odcloud.application.port.in.command;

import lombok.Builder;

@Builder
public record RegisterAccountCommand(

    String googleAuthorization,

    String name,

    String groupId,

    String newGroupName
) {

}
