package com.odcloud.fakeClass;

import com.odcloud.application.file.port.out.FileHistoryStoragePort;
import com.odcloud.domain.model.FileHistory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeFileHistoryStoragePort implements FileHistoryStoragePort {

    public List<FileHistory> database = new ArrayList<>();
    public Map<Long, LocalDateTime> updatedBackupDtMap = new HashMap<>();

    @Override
    public void save(FileHistory history) {
        database.add(history);
    }

    @Override
    public List<FileHistory> findByGroupId(Long groupId) {
        return database.stream()
            .filter(h -> h.getGroupId().equals(groupId))
            .toList();
    }

    @Override
    public List<FileHistory> findByGroupIdAndBackupDtIsNull(Long groupId) {
        return database.stream()
            .filter(h -> h.getGroupId().equals(groupId))
            .filter(h -> h.getBackupDt() == null)
            .toList();
    }

    @Override
    public void updateBackupDt(List<Long> ids, LocalDateTime backupDt) {
        ids.forEach(id -> updatedBackupDtMap.put(id, backupDt));
    }

    public void reset() {
        database.clear();
        updatedBackupDtMap.clear();
    }
}