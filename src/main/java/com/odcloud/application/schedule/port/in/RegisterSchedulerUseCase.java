package com.odcloud.application.schedule.port.in;

import com.odcloud.application.schedule.port.in.command.RegisterScheduleCommand;
import com.odcloud.application.schedule.service.register_schedule.RegisterScheduleServiceResponse;

public interface RegisterSchedulerUseCase {

    RegisterScheduleServiceResponse register(RegisterScheduleCommand command);
}
