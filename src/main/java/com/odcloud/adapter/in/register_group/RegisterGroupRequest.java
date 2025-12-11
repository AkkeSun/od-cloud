package com.odcloud.adapter.in.register_group;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record RegisterGroupRequest(
    @NotBlank(message = "그룹 아이디는 필수값 입니다")
    String id,

    @NotBlank(message = "그룹 설명은 필수값 입니다")
    String description
) {

    RegisterGroupCommand toCommand(Account account) {
        return RegisterGroupCommand.builder()
            .id(id)
            .ownerEmail(account.getEmail())
            .description(description)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
