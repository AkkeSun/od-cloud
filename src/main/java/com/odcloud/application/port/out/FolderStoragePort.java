package com.odcloud.application.port.out;

import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.Folder;
import java.util.List;

public interface FolderStoragePort {

    void save(Folder folder);

    Folder findById(Long id);

    List<Folder> findAll(FindFilesCommand command);

    boolean existsSameFolderName(Long parentId, String name);
}
