package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.RegisterScheduleCommand;
import com.odcloud.application.service.register_schedule.RegisterScheduleServiceResponse;

public interface RegisterSchedulerUseCase {

    RegisterScheduleServiceResponse register(RegisterScheduleCommand command);
}
