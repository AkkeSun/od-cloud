package com.odcloud.application.schedule.port.in.command;

import com.odcloud.domain.model.Account;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import lombok.Builder;

@Builder
public record FindSchedulesCommand(

    Account account,

    LocalDate baseDate,

    Long groupId
) {


    public LocalDateTime getStartDateTime() {
        return baseDate.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
    }

    public LocalDateTime getEndDateTime() {
        return baseDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
    }
}
