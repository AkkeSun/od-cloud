package com.odcloud.adapter.in.create_folder;

import com.odcloud.application.port.in.command.CreateFolderCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.ToStringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
record CreateFolderRequest(
    @NotNull(message = "상위 폴더 ID는 필수값 입니다")
    Long parentId,

    @NotBlank(message = "그룹 ID는 필수값 입니다")
    String groupId,

    @NotBlank(message = "폴더명은 필수값 입니다")
    String name,

    @NotBlank(message = "접근 레벨은 필수값 입니다")
    String accessLevel
) {

    CreateFolderCommand toCommand(Account account) {
        return CreateFolderCommand.builder()
            .parentId(parentId)
            .groupId(groupId)
            .name(name)
            .accessLevel(accessLevel)
            .owner(account.getEmail())
            .build();
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
