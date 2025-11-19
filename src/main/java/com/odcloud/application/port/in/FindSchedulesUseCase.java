package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.FindSchedulesCommand;
import com.odcloud.application.service.find_schedules.FindSchedulesServiceResponse;

public interface FindSchedulesUseCase {

    FindSchedulesServiceResponse findSchedules(FindSchedulesCommand command);
}
