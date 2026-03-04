package com.odcloud.application.account.service.register_account;

import lombok.Builder;

@Builder
public record RegisterAccountCommand(

    String googleAuthorization,

    String name,

    Long groupId,

    String newGroupName
) {

}
