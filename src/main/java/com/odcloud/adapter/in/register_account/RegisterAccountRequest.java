package com.odcloud.adapter.in.register_account;

import com.odcloud.application.port.in.command.RegisterAccountCommand;
import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record RegisterAccountRequest(
    @NotBlank(message = "이름은 필수값 입니다")
    String name,

    String groupId,

    String newGroupName
) {

    RegisterAccountCommand toCommand(String googleAuthorization) {
        return RegisterAccountCommand.builder()
            .googleAuthorization(googleAuthorization)
            .name(name)
            .groupId(groupId)
            .newGroupName(newGroupName)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
