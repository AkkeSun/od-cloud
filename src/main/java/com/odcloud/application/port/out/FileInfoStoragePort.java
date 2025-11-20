package com.odcloud.application.port.out;

import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.FileInfo;
import java.util.List;

public interface FileInfoStoragePort {

    void save(FileInfo file);

    FileInfo findById(Long id);

    List<FileInfo> findByIds(List<Long> ids);

    List<FileInfo> findAll(FindFilesCommand command);

    boolean existsByFolderIdAndName(Long folderId, String name);

    List<FileInfo> findByFolderId(Long folderId);

    void delete(FileInfo file);
}
