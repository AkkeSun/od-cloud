package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_FOLDER;

import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.domain.model.Folder;
import com.odcloud.infrastructure.exception.CustomBusinessException;
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

    @Override
    public Folder findById(Long id) {
        return folderRepository.findById(id).orElseThrow(
            () -> new CustomBusinessException(Business_DoesNotExists_FOLDER));
    }

    @Override
    public boolean existsSameFolderName(Long parentId, String name) {
        return folderRepository.existsSameFolderName(parentId, name);
    }
}
