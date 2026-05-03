package com.odcloud.application.account.service.register_account;

import lombok.Builder;

@Builder
public record RegisterAccountCommand(

    String googleAuthorization,

    Long groupId,

    String newGroupName
) {

}
