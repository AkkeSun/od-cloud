package com.odcloud.application.port.out;

import com.odcloud.domain.model.Folder;

public interface FolderStoragePort {

    void save(Folder folder);
}
