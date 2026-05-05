package com.odcloud.application.file.port.out;

import com.odcloud.domain.model.FileHistory;
import java.util.List;

public interface FileHistoryStoragePort {

    void save(FileHistory history);

    List<FileHistory> findByFileId(Long fileId);

    List<FileHistory> findByGroupId(Long groupId);

    void deleteByFileId(Long fileId);
}
