package com.odcloud.adapter.in.find_schedule;

import com.odcloud.application.service.find_schedule.FindScheduleServiceResponse;
import com.odcloud.domain.model.Schedule;
import com.odcloud.domain.model.ScheduleType;
import java.time.LocalDateTime;
import java.util.List;

record FindScheduleResponse(
    List<ScheduleDto> schedules
) {

    static FindScheduleResponse of(FindScheduleServiceResponse response) {
        List<ScheduleDto> scheduleDtos = response.schedules().stream()
            .map(ScheduleDto::from)
            .toList();
        return new FindScheduleResponse(scheduleDtos);
    }

    record ScheduleDto(
        Long id,
        String title,
        String description,
        LocalDateTime startDt,
        LocalDateTime endDt,
        ScheduleType scheduleType,
        Long accountId,
        Long groupId,
        Boolean notificationEnabled,
        Integer notificationMinutes,
        LocalDateTime regDt
    ) {

        static ScheduleDto from(Schedule schedule) {
            return new ScheduleDto(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getDescription(),
                schedule.getStartDt(),
                schedule.getEndDt(),
                schedule.getScheduleType(),
                schedule.getAccountId(),
                schedule.getGroupId(),
                schedule.getNotificationEnabled(),
                schedule.getNotificationMinutes(),
                schedule.getRegDt()
            );
        }
    }
}
