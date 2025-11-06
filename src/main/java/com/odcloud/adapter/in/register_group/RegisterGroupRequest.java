package com.odcloud.adapter.in.register_group;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record RegisterGroupRequest(
    @NotBlank(message = "그룹 아이디는 필수값 입니다")
    String id,

    @NotBlank(message = "그룹 설명은 필수값 입니다")
    String description
) {

    RegisterGroupCommand toCommand() {
        return RegisterGroupCommand.builder()
            .id(id)
            .description(description)
            .build();
    }
}
