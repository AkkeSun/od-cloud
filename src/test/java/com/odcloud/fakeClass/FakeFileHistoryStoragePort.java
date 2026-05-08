package com.odcloud.fakeClass;

import com.odcloud.application.file.port.out.FileHistoryStoragePort;
import com.odcloud.domain.model.FileHistory;
import java.util.ArrayList;
import java.util.List;

public class FakeFileHistoryStoragePort implements FileHistoryStoragePort {

    public List<FileHistory> database = new ArrayList<>();

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

    public void reset() {
        database.clear();
    }
}