package com.odcloud.application.schedule.port.in;

import com.odcloud.application.schedule.service.register_schedule.RegisterScheduleCommand;
import com.odcloud.application.schedule.service.register_schedule.RegisterScheduleResponse;

public interface RegisterSchedulerUseCase {

    RegisterScheduleResponse register(RegisterScheduleCommand command);
}
