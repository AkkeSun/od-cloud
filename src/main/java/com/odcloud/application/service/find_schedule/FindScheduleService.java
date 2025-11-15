package com.odcloud.application.service.find_schedule;

import com.odcloud.application.port.in.FindScheduleUseCase;
import com.odcloud.application.port.in.command.FindScheduleCommand;
import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.domain.model.SchedulePeriod;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class FindScheduleService implements FindScheduleUseCase {

    private final ScheduleStoragePort scheduleStoragePort;

    @Override
    @Transactional(readOnly = true)
    public FindScheduleServiceResponse findSchedules(FindScheduleCommand command) {
        // 기간 계산
        LocalDateTime startDate = calculateStartDate(command.period(), command.baseDate());
        LocalDateTime endDate = calculateEndDate(command.period(), command.baseDate());

        log.info("[FindScheduleService] 일정 조회: period={}, baseDate={}, startDate={}, endDate={}",
            command.period(), command.baseDate(), startDate, endDate);

        List<Schedule> schedules = scheduleStoragePort.findByConditions(
            command.accountId(),
            command.scheduleType(),
            startDate,
            endDate,
            command.groupIds()
        );

        return FindScheduleServiceResponse.of(schedules);
    }

    private LocalDateTime calculateStartDate(SchedulePeriod period, LocalDate baseDate) {
        return switch (period) {
            case DAILY -> baseDate.atStartOfDay();
            case WEEKLY -> baseDate.with(DayOfWeek.MONDAY).atStartOfDay();
            case MONTHLY -> baseDate.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        };
    }

    private LocalDateTime calculateEndDate(SchedulePeriod period, LocalDate baseDate) {
        return switch (period) {
            case DAILY -> baseDate.atTime(23, 59, 59);
            case WEEKLY -> baseDate.with(DayOfWeek.SUNDAY).atTime(23, 59, 59);
            case MONTHLY ->
                baseDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
        };
    }
}
