package com.odcloud.application.port.out;

import com.odcloud.application.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.FolderInfo;
import java.util.List;

public interface FolderInfoStoragePort {

    void save(FolderInfo folder);

    FolderInfo findById(Long id);

    List<FolderInfo> findAll(FindFilesCommand command);

    boolean existsSameFolderName(Long parentId, String name);

    List<FolderInfo> findByParentId(Long parentId);

    void delete(FolderInfo folder);
}
