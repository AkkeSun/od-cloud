package com.odcloud.application.service.delete_schedule;

import com.odcloud.application.port.in.DeleteScheduleUseCase;
import com.odcloud.application.port.in.command.DeleteScheduleCommand;
import com.odcloud.application.port.out.ScheduleStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class DeleteScheduleService implements DeleteScheduleUseCase {

    private final ScheduleStoragePort scheduleStoragePort;

    @Override
    @Transactional
    public DeleteScheduleServiceResponse delete(DeleteScheduleCommand command) {
        // 권한 체크: 본인 일정만 삭제 가능
        if (!scheduleStoragePort.existsByIdAndAccountId(command.scheduleId(),
            command.accountId())) {
            throw new IllegalArgumentException("일정을 삭제할 권한이 없습니다.");
        }

        scheduleStoragePort.deleteById(command.scheduleId());
        log.info("[DeleteScheduleService] 일정 삭제 완료: scheduleId={}, accountId={}",
            command.scheduleId(), command.accountId());

        return DeleteScheduleServiceResponse.ofSuccess();
    }
}
