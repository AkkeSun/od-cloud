package com.odcloud.adapter.in.controller.account.register_account;

import com.odcloud.application.account.service.register_account.RegisterAccountCommand;
import com.odcloud.infrastructure.util.StringUtil;
import lombok.Builder;

@Builder
record RegisterAccountRequest(
    Long groupId,

    String newGroupName
) {

    RegisterAccountCommand toCommand(String googleAuthorization) {
        return RegisterAccountCommand.builder()
            .googleAuthorization(googleAuthorization)
            .groupId(groupId)
            .newGroupName(newGroupName)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
