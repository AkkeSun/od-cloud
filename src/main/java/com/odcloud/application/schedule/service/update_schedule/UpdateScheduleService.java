package com.odcloud.application.schedule.service.update_schedule;

import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;

import com.odcloud.application.schedule.port.in.UpdateScheduleUseCase;
import com.odcloud.application.schedule.port.in.command.UpdateScheduleCommand;
import com.odcloud.application.schedule.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class UpdateScheduleService implements UpdateScheduleUseCase {

    private final ScheduleStoragePort scheduleStoragePort;

    @Override
    @Transactional
    public UpdateScheduleServiceResponse update(UpdateScheduleCommand command) {
        Schedule schedule = scheduleStoragePort.findById(command.scheduleId());

        if (schedule.isGroupSchedule() && !command.account().getGroupIds()
            .contains(schedule.getGroupId())) {
            throw new CustomAuthorizationException(ACCESS_DENIED);
        } else if (!schedule.isGroupSchedule() && !schedule.getWriterEmail()
            .equals(command.account().getEmail())) {
            throw new CustomAuthorizationException(ACCESS_DENIED);
        }

        schedule.update(command);
        scheduleStoragePort.save(schedule);

        if (schedule.isGroupSchedule()) {
            // todo: 그룹 일정 수정 시 알림 발송
        }
        return UpdateScheduleServiceResponse.ofSuccess();
    }
}
