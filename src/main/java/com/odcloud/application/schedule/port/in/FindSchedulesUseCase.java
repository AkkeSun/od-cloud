package com.odcloud.application.schedule.port.in;

import com.odcloud.application.schedule.service.find_schedules.FindSchedulesCommand;
import com.odcloud.application.schedule.service.find_schedules.FindSchedulesResponse;

public interface FindSchedulesUseCase {

    FindSchedulesResponse findSchedules(FindSchedulesCommand command);
}
