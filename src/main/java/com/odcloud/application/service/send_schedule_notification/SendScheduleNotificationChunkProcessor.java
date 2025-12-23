package com.odcloud.application.service.send_schedule_notification;

import com.odcloud.application.port.in.PushFcmUseCase;
import com.odcloud.application.port.in.command.PushFcmCommand;
import com.odcloud.application.port.out.AccountDeviceStoragePort;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.AccountDevice;
import com.odcloud.domain.model.Schedule;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
class SendScheduleNotificationChunkProcessor {

    private final PushFcmUseCase pushFcmUseCase;
    private final GroupStoragePort groupStoragePort;
    private final ScheduleStoragePort scheduleStoragePort;
    private final AccountDeviceStoragePort accountDeviceStoragePort;

    @Transactional
    public void processChunk(List<Schedule> chunk) {
        List<Long> processedIds = new ArrayList<>();

        for (Schedule schedule : chunk) {
            try {
                List<AccountDevice> devices = schedule.isGroupSchedule() ?
                    accountDeviceStoragePort.findByGroupIdForPush(schedule.getGroupId()) :
                    accountDeviceStoragePort.findByAccountEmailForPush(schedule.getWriterEmail());

                if (devices.isEmpty()) {
                    continue;
                }

                String fcmTitle = schedule.isGroupSchedule() ?
                    groupStoragePort.findById(schedule.getGroupId()).getName() : "일정 알림";

                pushFcmUseCase.push(
                    PushFcmCommand.ofNotificationSchedule(schedule, devices, fcmTitle));

                processedIds.add(schedule.getId());
            } catch (Exception e) {
                log.error("Failed to send push notification for schedule: {}", schedule.getId(), e);
            }
        }

        if (!processedIds.isEmpty()) {
            scheduleStoragePort.updateNotificationYn(processedIds);
        }
    }
}
