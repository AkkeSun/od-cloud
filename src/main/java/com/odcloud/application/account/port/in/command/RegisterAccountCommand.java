package com.odcloud.application.account.port.in.command;

import lombok.Builder;

@Builder
public record RegisterAccountCommand(

    String googleAuthorization,

    String name,

    Long groupId,

    String newGroupName
) {

}
