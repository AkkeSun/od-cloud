package com.odcloud.application.service.delete_schedule;

import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;

import com.odcloud.application.port.in.DeleteScheduleUseCase;
import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class DeleteScheduleService implements DeleteScheduleUseCase {

    private final ScheduleStoragePort scheduleStoragePort;

    @Override
    @Transactional
    public DeleteScheduleServiceResponse delete(Long scheduleId, Account account) {
        Schedule schedule = scheduleStoragePort.findById(scheduleId);

        if (schedule.isGroupSchedule() && !account.getGroupIds().contains(schedule.getGroupId())) {
            throw new CustomAuthorizationException(ACCESS_DENIED);
        } else if (!schedule.getWriterEmail().equals(account.getEmail())) {
            throw new CustomAuthorizationException(ACCESS_DENIED);
        }

        scheduleStoragePort.delete(schedule);
        if (schedule.isGroupSchedule()) {
            // todo: 알림 발송
        }
        return DeleteScheduleServiceResponse.ofSuccess();
    }
}
