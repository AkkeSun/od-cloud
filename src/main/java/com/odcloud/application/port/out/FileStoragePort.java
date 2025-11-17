package com.odcloud.application.port.out;

import com.odcloud.domain.model.File;
import java.util.List;

public interface FileStoragePort {

    void save(File file);

    File findById(Long id);

    List<File> findByIds(List<Long> ids);

    boolean existsByFolderIdAndName(Long folderId, String name);
}
