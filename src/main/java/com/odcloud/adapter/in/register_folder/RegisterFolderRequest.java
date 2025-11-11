package com.odcloud.adapter.in.register_folder;

import com.odcloud.application.port.in.command.RegisterFolderCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.ToStringUtil;
import com.odcloud.infrastructure.validation.Contains;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.CustomGroups;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.NotBlankGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
record RegisterFolderRequest(
    @NotNull(message = "상위 폴더 ID는 필수값 입니다", groups = NotBlankGroups.class)
    Long parentId,

    @NotBlank(message = "그룹 ID는 필수값 입니다", groups = NotBlankGroups.class)
    String groupId,

    @NotBlank(message = "폴더명은 필수값 입니다", groups = NotBlankGroups.class)
    String name,

    @NotBlank(message = "접근 범위는 필수값 입니다", groups = NotBlankGroups.class)
    @Contains(
        values = {"PRIVATE", "PUBLIC"},
        message = "유효하지 않은 접근 범위 입니다",
        groups = CustomGroups.class)
    String accessLevel
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
        return ToStringUtil.toString(this);
    }
}
