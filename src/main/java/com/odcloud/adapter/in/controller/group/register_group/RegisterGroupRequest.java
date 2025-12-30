package com.odcloud.adapter.in.controller.group.register_group;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record RegisterGroupRequest(
    @NotBlank(message = "그룹 이름은 필수값 입니다")
    String name
) {

    RegisterGroupCommand toCommand(Account account) {
        return RegisterGroupCommand.builder()
            .ownerEmail(account.getEmail())
            .name(name)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
