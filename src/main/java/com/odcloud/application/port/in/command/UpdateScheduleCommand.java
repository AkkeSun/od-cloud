package com.odcloud.application.port.in.command;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UpdateScheduleCommand(
    Long scheduleId,
    Long accountId,  // 권한 체크용
    String title,
    String description,
    LocalDateTime startDt,
    LocalDateTime endDt,
    Boolean notificationEnabled,
    Integer notificationMinutes
) {

}
