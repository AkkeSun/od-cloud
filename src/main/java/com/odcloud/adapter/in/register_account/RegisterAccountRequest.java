package com.odcloud.adapter.in.register_account;

import com.odcloud.application.port.in.command.RegisterAccountCommand;
import com.odcloud.infrastructure.util.ToStringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
record RegisterAccountRequest(
    @NotBlank(message = "이름은 필수값 입니다")
    String name,

    @NotEmpty(message = "그룹은 필수값 입니다")
    String group
) {

    RegisterAccountCommand toCommand(String googleAuthorization) {
        return RegisterAccountCommand.builder()
            .googleAuthorization(googleAuthorization)
            .name(name)
            .group(group)
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
