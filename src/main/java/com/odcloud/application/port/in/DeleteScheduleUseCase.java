package com.odcloud.application.port.in;

import com.odcloud.application.service.delete_schedule.DeleteScheduleServiceResponse;
import com.odcloud.domain.model.Account;

public interface DeleteScheduleUseCase {

    DeleteScheduleServiceResponse delete(Long scheduleId, Account account);
}
