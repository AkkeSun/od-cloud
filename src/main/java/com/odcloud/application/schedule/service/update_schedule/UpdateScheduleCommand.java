package com.odcloud.application.schedule.service.update_schedule;

import com.odcloud.domain.model.Account;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UpdateScheduleCommand(

    Long scheduleId,

    Account account,

    String content,

    LocalDateTime startDt,

    LocalDateTime notificationDt
) {

}
