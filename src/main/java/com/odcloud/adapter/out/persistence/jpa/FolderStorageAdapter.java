package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.domain.model.Folder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class FolderStorageAdapter implements FolderStoragePort {

    private final FolderRepository folderRepository;

    @Override
    public void save(Folder folder) {
        folderRepository.save(folder);
    }
}
