package com.odcloud.application.service.register_schedule;

import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;

import com.odcloud.application.port.in.PushFcmUseCase;
import com.odcloud.application.port.in.RegisterSchedulerUseCase;
import com.odcloud.application.port.in.command.PushFcmCommand;
import com.odcloud.application.port.in.command.RegisterScheduleCommand;
import com.odcloud.application.port.out.AccountDeviceStoragePort;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.AccountDevice;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class RegisterScheduleService implements RegisterSchedulerUseCase {

    private final PushFcmUseCase pushFcmUseCase;
    private final GroupStoragePort groupStoragePort;
    private final ScheduleStoragePort scheduleStoragePort;
    private final AccountDeviceStoragePort accountDeviceStoragePort;

    @Override
    @Transactional
    public RegisterScheduleServiceResponse register(RegisterScheduleCommand command) {
        if (command.groupAccessDenied()) {
            throw new CustomAuthenticationException(ACCESS_DENIED);
        }

        scheduleStoragePort.save(Schedule.of(command));
        if (command.isGroupSchedule()) {
            List<AccountDevice> devices = accountDeviceStoragePort
                .findByGroupIdForPush(command.groupId())
                .stream()
                .filter(device -> !device.getAccountId().equals(command.account().getId()))
                .toList();

            if (!devices.isEmpty()) {
                Group group = groupStoragePort.findById(command.groupId());
                pushFcmUseCase.pushAsync(
                    PushFcmCommand.ofNewSchedule(devices, group, command.startDt()));
            }
        }

        return RegisterScheduleServiceResponse.ofSuccess();
    }
}
