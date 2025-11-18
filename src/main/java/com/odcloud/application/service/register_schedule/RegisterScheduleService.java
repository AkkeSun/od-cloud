package com.odcloud.application.service.register_schedule;

import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;

import com.odcloud.application.port.in.RegisterSchedulerUseCase;
import com.odcloud.application.port.in.command.RegisterScheduleCommand;
import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterScheduleService implements RegisterSchedulerUseCase {

    private final ScheduleStoragePort scheduleStoragePort;

    @Override
    public RegisterScheduleServiceResponse register(RegisterScheduleCommand command) {
        if (command.groupAccessDenied()) {
            throw new CustomAuthenticationException(ACCESS_DENIED);
        }

        scheduleStoragePort.save(Schedule.of(command));
        if (command.isGroupSchedule()) {
            // todo : 그룹 스케쥴의 경우 알림 발송
        }
        return RegisterScheduleServiceResponse.ofSuccess();
    }
}
