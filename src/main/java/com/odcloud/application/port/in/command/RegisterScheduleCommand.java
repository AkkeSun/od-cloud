package com.odcloud.application.port.in.command;

import com.odcloud.domain.model.ScheduleType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RegisterScheduleCommand(
    String title,
    String description,
    LocalDateTime startDt,
    LocalDateTime endDt,
    ScheduleType scheduleType,
    Long accountId,
    Long groupId,
    Boolean notificationEnabled,
    Integer notificationMinutes
) {

}
