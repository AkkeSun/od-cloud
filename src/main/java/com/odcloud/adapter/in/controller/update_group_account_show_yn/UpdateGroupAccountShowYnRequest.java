package com.odcloud.adapter.in.controller.update_group_account_show_yn;

import com.odcloud.application.port.in.command.UpdateGroupAccountUseYnCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.StringUtil;
import com.odcloud.infrastructure.validation.Contains;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.CustomGroups;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.NotBlankGroups;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record UpdateGroupAccountShowYnRequest(

    @Contains(message = "유효하지 않은 노출 유무 입니다", groups = CustomGroups.class)
    @NotBlank(message = "노출 유무는 필수값 입니다", groups = NotBlankGroups.class)
    String showYn
) {

    public UpdateGroupAccountUseYnCommand toCommand(String groupId, Account account) {
        return UpdateGroupAccountUseYnCommand.builder()
            .groupId(groupId)
            .account(account)
            .showYn(showYn)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
