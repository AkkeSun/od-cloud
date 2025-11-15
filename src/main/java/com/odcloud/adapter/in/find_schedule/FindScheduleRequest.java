package com.odcloud.adapter.in.find_schedule;

import com.odcloud.application.port.in.command.FindScheduleCommand;
import com.odcloud.domain.model.SchedulePeriod;
import com.odcloud.domain.model.ScheduleType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class FindScheduleRequest {

    @NotNull(message = "계정 ID는 필수값입니다")
    private Long accountId;

    @NotNull(message = "기간 타입은 필수값입니다")
    private SchedulePeriod period;

    @NotNull(message = "기준 날짜는 필수값입니다")
    private LocalDate baseDate;

    private ScheduleType scheduleType;  // null이면 전체

    private List<Long> groupIds;  // 그룹 일정 조회 시

    FindScheduleCommand toCommand() {
        return FindScheduleCommand.builder()
            .accountId(accountId)
            .period(period)
            .baseDate(baseDate)
            .scheduleType(scheduleType)
            .groupIds(groupIds)
            .build();
    }
}
