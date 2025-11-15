package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.DeleteScheduleCommand;
import com.odcloud.application.service.delete_schedule.DeleteScheduleServiceResponse;

public interface DeleteScheduleUseCase {

    DeleteScheduleServiceResponse delete(DeleteScheduleCommand command);
}
