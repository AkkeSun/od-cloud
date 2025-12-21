package com.odcloud.adapter.in.controller.register_schedule;

import com.odcloud.application.port.in.command.RegisterScheduleCommand;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.DateUtil;
import com.odcloud.infrastructure.util.StringUtil;
import com.odcloud.infrastructure.validation.DateTimePattern;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.CustomGroups;
import com.odcloud.infrastructure.validation.groups.ValidationGroups.NotBlankGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
record RegisterScheduleRequest(

    @NotBlank(message = "내용은 필수값입니다", groups = NotBlankGroups.class)
    String content,

    @NotNull(message = "시작일시는 필수값입니다", groups = NotBlankGroups.class)
    @DateTimePattern(message = "유효하지 않은 사직알시 형식 입니다", groups = CustomGroups.class)
    String startDt,

    String groupId,

    String notificationDt
) {

    RegisterScheduleCommand toCommand(Account account) {
        return RegisterScheduleCommand.builder()
            .account(account)
            .content(content)
            .startDt(DateUtil.parse(startDt))
            .groupId(groupId)
            .notificationDt(notificationDt == null ? null : DateUtil.parse(notificationDt))
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
