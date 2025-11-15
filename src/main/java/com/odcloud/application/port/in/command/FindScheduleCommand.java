package com.odcloud.application.port.in.command;

import com.odcloud.domain.model.SchedulePeriod;
import com.odcloud.domain.model.ScheduleType;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record FindScheduleCommand(
    Long accountId,          // 조회 주체
    SchedulePeriod period,   // 일간/주간/월간
    LocalDate baseDate,      // 기준 날짜 (해당 날짜가 속한 일/주/월)
    ScheduleType scheduleType,  // PERSONAL/GROUP/null(전체)
    List<Long> groupIds      // 그룹 일정 조회 시 필요
) {

}
