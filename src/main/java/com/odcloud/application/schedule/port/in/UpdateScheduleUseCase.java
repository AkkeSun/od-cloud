package com.odcloud.application.schedule.port.in;

import com.odcloud.application.schedule.port.in.command.UpdateScheduleCommand;
import com.odcloud.application.schedule.service.update_schedule.UpdateScheduleServiceResponse;

public interface UpdateScheduleUseCase {

    UpdateScheduleServiceResponse update(UpdateScheduleCommand command);
}
