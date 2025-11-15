package com.odcloud.application.service.find_schedule;

import com.odcloud.domain.model.Schedule;
import java.util.List;

public record FindScheduleServiceResponse(
    List<Schedule> schedules
) {

    public static FindScheduleServiceResponse of(List<Schedule> schedules) {
        return new FindScheduleServiceResponse(schedules);
    }
}
