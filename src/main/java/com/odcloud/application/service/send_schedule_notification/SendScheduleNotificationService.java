package com.odcloud.application.service.send_schedule_notification;

import com.odcloud.application.port.in.SendScheduleNotificationUseCase;
import com.odcloud.application.port.out.ScheduleStoragePort;
import com.odcloud.domain.model.Schedule;
import com.odcloud.infrastructure.util.CollectionUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class SendScheduleNotificationService implements SendScheduleNotificationUseCase {

    private static final int CHUNK_SIZE = 50;
    private final Executor asyncExecutor;
    private final ScheduleStoragePort scheduleStoragePort;
    private final SendScheduleNotificationChunkProcessor chunkProcessor;

    @Override
    public void sendNotifications() {
        List<Schedule> schedules = scheduleStoragePort
            .findSchedulesForNotification(LocalDateTime.now());

        if (schedules.isEmpty()) {
            return;
        }

        log.info("Found {} schedules to send notification", schedules.size());
        List<List<Schedule>> chunks = CollectionUtil.partition(schedules, CHUNK_SIZE);
        List<CompletableFuture<Void>> futures = chunks.stream()
            .map(chunk -> CompletableFuture.runAsync(() -> chunkProcessor.processChunk(chunk),
                asyncExecutor))
            .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}
