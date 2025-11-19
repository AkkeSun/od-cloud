package com.odcloud.application.port.in.command;

import com.odcloud.domain.model.Account;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UpdateScheduleCommand(

    Long scheduleId,

    Account account,

    String content,

    LocalDateTime startDt,

    LocalDateTime endDt,

    LocalDateTime notificationDt
) {
}
