package com.odcloud.adapter.in.scheduler;

import com.odcloud.application.file.port.in.BackupGroupFilesUseCase;
import com.odcloud.application.file.service.backup_group_files.BackupGroupFilesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class BackupScheduler {

    private final BackupGroupFilesUseCase backupGroupFilesUseCase;

    @Scheduled(cron = "0 0 2 * * *")
    @SchedulerLock(
        name = "BackupScheduler_backupGroupFiles",
        lockAtLeastFor = "1m",
        lockAtMostFor = "30m"
    )
    public void backupGroupFiles() {
        log.info("[BackupScheduler] Google Drive 백업 시작");
        BackupGroupFilesResponse result = backupGroupFilesUseCase.backup();
        log.info("[BackupScheduler] 완료 - totalGroups={}, success={}, fail={}",
            result.totalGroups(), result.successCount(), result.failCount());
    }
}
