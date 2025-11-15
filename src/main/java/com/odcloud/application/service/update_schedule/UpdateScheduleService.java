package com.odcloud.application.service.update_schedule;

import com.odcloud.application.port.in.UpdateScheduleUseCase;
import com.odcloud.application.port.in.command.UpdateScheduleCommand;
import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class UpdateScheduleService implements UpdateScheduleUseCase {

    private final ScheduleStoragePort scheduleStoragePort;

    @Override
    @Transactional
    public UpdateScheduleServiceResponse update(UpdateScheduleCommand command) {
        // 권한 체크: 본인 일정만 수정 가능
        if (!scheduleStoragePort.existsByIdAndAccountId(command.scheduleId(),
            command.accountId())) {
            throw new IllegalArgumentException("일정을 수정할 권한이 없습니다.");
        }

        Schedule schedule = scheduleStoragePort.findById(command.scheduleId());

        schedule.update(
            command.title(),
            command.description(),
            command.startDt(),
            command.endDt(),
            command.notificationEnabled(),
            command.notificationMinutes()
        );

        scheduleStoragePort.save(schedule);
        log.info("[UpdateScheduleService] 일정 수정 완료: scheduleId={}, accountId={}",
            command.scheduleId(), command.accountId());

        return UpdateScheduleServiceResponse.ofSuccess();
    }
}
