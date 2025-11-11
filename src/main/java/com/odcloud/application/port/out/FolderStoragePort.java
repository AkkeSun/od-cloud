package com.odcloud.application.port.out;

import com.odcloud.domain.model.Folder;

public interface FolderStoragePort {

    void save(Folder folder);

    Folder findById(Long id);

    boolean existsSameFolderName(Long parentId, String name);
}
