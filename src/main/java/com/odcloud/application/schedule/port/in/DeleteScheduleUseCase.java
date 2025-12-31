package com.odcloud.application.schedule.port.in;

import com.odcloud.application.schedule.service.delete_schedule.DeleteScheduleServiceResponse;
import com.odcloud.domain.model.Account;

public interface DeleteScheduleUseCase {

    DeleteScheduleServiceResponse delete(Long scheduleId, Account account);
}
