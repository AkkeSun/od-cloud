package com.odcloud.adapter.in.controller.update_schedule;

import com.odcloud.application.port.in.command.UpdateScheduleCommand;
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
record UpdateScheduleRequest(

    @NotBlank(message = "내용은 필수값입니다", groups = NotBlankGroups.class)
    String content,

    @NotNull(message = "시작일시는 필수값입니다", groups = NotBlankGroups.class)
    @DateTimePattern(message = "유효하지 않은 시작일시 형식 입니다", groups = CustomGroups.class)
    String startDt,

    String notificationDt
) {

    UpdateScheduleCommand toCommand(Long scheduleId, Account account) {
        return UpdateScheduleCommand.builder()
            .scheduleId(scheduleId)
            .account(account)
            .content(content)
            .startDt(DateUtil.parse(startDt))
            .notificationDt(notificationDt == null ? null : DateUtil.parse(notificationDt))
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
