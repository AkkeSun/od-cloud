package com.odcloud.application.schedule.port.in;

import com.odcloud.application.schedule.port.in.command.FindSchedulesCommand;
import com.odcloud.application.schedule.service.find_schedules.FindSchedulesServiceResponse;

public interface FindSchedulesUseCase {

    FindSchedulesServiceResponse findSchedules(FindSchedulesCommand command);
}
