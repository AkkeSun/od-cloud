package com.odcloud.application.schedule.port.in;

import com.odcloud.application.schedule.service.delete_schedule.DeleteScheduleResponse;
import com.odcloud.domain.model.Account;

public interface DeleteScheduleUseCase {

    DeleteScheduleResponse delete(Long scheduleId, Account account);
}
