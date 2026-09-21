package com.odcloud.application.file.port.out;

import com.odcloud.domain.model.FileHistory;
import java.time.LocalDateTime;
import java.util.List;

public interface FileHistoryStoragePort {

    void save(FileHistory history);

    List<FileHistory> findByGroupId(Long groupId);

    List<FileHistory> findByGroupIdAndBackupDtIsNull(Long groupId, LocalDateTime regDtAfter);

    void updateBackupDt(List<Long> ids, LocalDateTime backupDt);
}
