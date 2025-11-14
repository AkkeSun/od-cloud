package com.odcloud.application.port.out;

import com.odcloud.domain.model.Folder;
import java.util.List;

public interface FolderStoragePort {

    void save(Folder folder);

    Folder findById(Long id);

    boolean existsSameFolderName(Long parentId, String name);

    List<Folder> findByParentId(Long parentId);

    List<Folder> findAllSubFolders(Long folderId);
}
