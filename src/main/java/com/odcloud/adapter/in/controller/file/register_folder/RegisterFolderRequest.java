package com.odcloud.adapter.in.controller.file.register_folder;

import com.odcloud.application.file.service.register_folder.RegisterFolderCommand;
import com.odcloud.infrastructure.util.StringUtil;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.NotBlankGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
record RegisterFolderRequest(
    @NotNull(message = "상위 폴더 ID는 필수값 입니다", groups = NotBlankGroups.class)
    Long parentId,

    @NotNull(message = "그룹 ID는 필수값 입니다", groups = NotBlankGroups.class)
    Long groupId,

    @NotBlank(message = "폴더명은 필수값 입니다", groups = NotBlankGroups.class)
    String name
) {

    RegisterFolderCommand toCommand(String owner) {
        return RegisterFolderCommand.builder()
            .parentId(parentId)
            .groupId(groupId)
            .name(name)
            .owner(owner)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
