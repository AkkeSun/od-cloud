package com.odcloud.application.port.out;

import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.File;
import java.util.List;

public interface FileStoragePort {

    void save(File file);

    File findById(Long id);

    List<File> findByIds(List<Long> ids);

    List<File> findAll(FindFilesCommand command);

    boolean existsByFolderIdAndName(Long folderId, String name);
}
