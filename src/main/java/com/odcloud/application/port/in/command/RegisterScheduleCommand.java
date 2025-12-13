package com.odcloud.application.port.in.command;

import com.odcloud.domain.model.Account;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RegisterScheduleCommand(

    String content,

    LocalDateTime startDt,

    Account account,

    String groupId,

    LocalDateTime notificationDt
) {

    public boolean isGroupSchedule() {
        return groupId != null;
    }

    public boolean groupAccessDenied() {
        return groupId != null && !account().getGroupIds().contains(groupId);
    }
}
