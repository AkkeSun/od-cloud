package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.UpdateScheduleCommand;
import com.odcloud.application.service.update_schedule.UpdateScheduleServiceResponse;

public interface UpdateScheduleUseCase {

    UpdateScheduleServiceResponse update(UpdateScheduleCommand command);
}
