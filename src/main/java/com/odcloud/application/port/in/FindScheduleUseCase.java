package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.FindScheduleCommand;
import com.odcloud.application.service.find_schedule.FindScheduleServiceResponse;

public interface FindScheduleUseCase {

    FindScheduleServiceResponse findSchedules(FindScheduleCommand command);
}
