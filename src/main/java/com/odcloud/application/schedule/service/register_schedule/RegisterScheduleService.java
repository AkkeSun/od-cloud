package com.odcloud.application.schedule.service.register_schedule;

import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;

import com.odcloud.application.schedule.port.in.RegisterSchedulerUseCase;
import com.odcloud.application.schedule.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class RegisterScheduleService implements RegisterSchedulerUseCase {

    private final ScheduleStoragePort scheduleStoragePort;

    @Override
    @Transactional
    public RegisterScheduleResponse register(RegisterScheduleCommand command) {
        if (command.groupAccessDenied()) {
            throw new CustomAuthenticationException(ACCESS_DENIED);
        }

        scheduleStoragePort.save(Schedule.builder()
            .writerEmail(command.account().getEmail())
            .content(command.content())
            .startDt(command.startDt())
            .groupId(command.groupId())
            .notificationDt(command.notificationDt())
            .notificationYn("N")
            .regDt(LocalDateTime.now())
            .build());

        return RegisterScheduleResponse.ofSuccess();
    }
}
