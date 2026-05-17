package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.file.port.out.FileHistoryStoragePort;
import com.odcloud.domain.model.FileHistory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class FileHistoryStorageAdapter implements FileHistoryStoragePort {

    private final FileHistoryRepository fileHistoryRepository;

    @Override
    public void save(FileHistory history) {
        fileHistoryRepository.save(history);
    }

    @Override
    public List<FileHistory> findByGroupId(Long groupId) {
        return fileHistoryRepository.findByGroupId(groupId);
    }

    @Override
    public List<FileHistory> findByGroupIdAndBackupDtIsNull(Long groupId) {
        return fileHistoryRepository.findByGroupIdAndBackupDtIsNull(groupId);
    }

    @Override
    public void updateBackupDt(List<Long> ids, LocalDateTime backupDt) {
        fileHistoryRepository.updateBackupDt(ids, backupDt);
    }
}
