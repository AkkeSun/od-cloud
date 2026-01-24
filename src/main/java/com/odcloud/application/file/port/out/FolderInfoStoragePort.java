package com.odcloud.application.file.port.out;

import com.odcloud.application.file.port.in.command.FindFilesCommand;
import com.odcloud.domain.model.FolderInfo;
import java.util.List;

public interface FolderInfoStoragePort {

    void save(FolderInfo folder);

    FolderInfo findById(Long id);

    List<FolderInfo> findAll(FindFilesCommand command);

    boolean existsSameFolderName(Long parentId, String name);

    boolean existsById(Long id);

    List<FolderInfo> findByParentId(Long parentId);

    void delete(FolderInfo folder);

    FolderInfo findRootFolderByGroupId(Long groupId);

    List<FolderInfo> findByGroupId(Long groupId);

    void deleteByGroupId(Long groupId);
}
