package com.odcloud.application.schedule.port.in;

import com.odcloud.application.schedule.service.update_schedule.UpdateScheduleCommand;
import com.odcloud.application.schedule.service.update_schedule.UpdateScheduleResponse;

public interface UpdateScheduleUseCase {

    UpdateScheduleResponse update(UpdateScheduleCommand command);
}
