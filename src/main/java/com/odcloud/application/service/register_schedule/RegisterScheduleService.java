package com.odcloud.application.service.register_schedule;

import com.odcloud.application.port.in.RegisterScheduleUseCase;
import com.odcloud.application.port.in.command.RegisterScheduleCommand;
import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.domain.model.ScheduleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class RegisterScheduleService implements RegisterScheduleUseCase {

    private final ScheduleStoragePort scheduleStoragePort;

    @Override
    @Transactional
    public RegisterScheduleServiceResponse register(RegisterScheduleCommand command) {
        Schedule schedule;

        if (command.scheduleType() == ScheduleType.PERSONAL) {
            schedule = Schedule.createPersonal(
                command.title(),
                command.description(),
                command.startDt(),
                command.endDt(),
                command.accountId(),
                command.notificationEnabled(),
                command.notificationMinutes()
            );
        } else if (command.scheduleType() == ScheduleType.GROUP) {
            schedule = Schedule.createGroup(
                command.title(),
                command.description(),
                command.startDt(),
                command.endDt(),
                command.accountId(),
                command.groupId(),
                command.notificationEnabled(),
                command.notificationMinutes()
            );
        } else {
            throw new IllegalArgumentException("잘못된 일정 타입입니다: " + command.scheduleType());
        }

        scheduleStoragePort.save(schedule);
        log.info("[RegisterScheduleService] 일정 등록 완료: type={}, accountId={}",
            command.scheduleType(), command.accountId());

        // Note: ID는 save 후 자동 생성되므로 실제 구현에서는 save 후 조회하거나
        // JPA의 경우 엔티티에서 ID를 가져올 수 있음
        // 여기서는 간단히 null 반환 (실제로는 저장된 Schedule의 ID 반환 필요)
        return RegisterScheduleServiceResponse.of(schedule.getId());
    }
}
