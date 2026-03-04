package com.odcloud.adapter.in.controller.group.register_group;

import com.odcloud.application.group.service.register_group.RegisterGroupCommand;
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
            .name(name)
            .ownerEmail(account.getEmail())
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
