package com.odcloud.adapter.in.scheduler;

import com.odcloud.application.schedule.port.in.SendScheduleNotificationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class PushScheduler {

    private final SendScheduleNotificationUseCase sendScheduleNotificationUseCase;

    //@Scheduled(fixedDelay = 30000) // 30초
    @SchedulerLock(name = "PushScheduler_sendScheduleNotifications", lockAtLeastFor = "5s", lockAtMostFor = "15s")
    public void sendScheduleNotifications() {
        sendScheduleNotificationUseCase.sendNotifications();
    }
}
