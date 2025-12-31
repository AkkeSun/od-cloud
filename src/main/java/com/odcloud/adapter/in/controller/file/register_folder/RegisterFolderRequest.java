package com.odcloud.adapter.in.controller.file.register_folder;

import com.odcloud.application.file.port.in.command.RegisterFolderCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.StringUtil;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.NotBlankGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
record RegisterFolderRequest(
    @NotNull(message = "상위 폴더 ID는 필수값 입니다", groups = NotBlankGroups.class)
    Long parentId,

    @NotBlank(message = "그룹 ID는 필수값 입니다", groups = NotBlankGroups.class)
    String groupId, // 위에서 받아오는걸로 수정

    @NotBlank(message = "폴더명은 필수값 입니다", groups = NotBlankGroups.class)
    String name
) {

    RegisterFolderCommand toCommand(Account account) {
        return RegisterFolderCommand.builder()
            .parentId(parentId)
            .groupId(groupId)
            .name(name)
            .owner(account.getEmail())
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
