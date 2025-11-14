package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_FOLDER;

import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.domain.model.Folder;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public List<Folder> findByParentId(Long parentId) {
        return folderRepository.findByParentId(parentId);
    }

    @Override
    public List<Folder> findAllSubFolders(Long folderId) {
        List<Folder> allSubFolders = new ArrayList<>();
        collectSubFoldersRecursively(folderId, allSubFolders);
        return allSubFolders;
    }

    private void collectSubFoldersRecursively(Long parentId, List<Folder> result) {
        List<Folder> children = folderRepository.findByParentId(parentId);
        result.addAll(children);
        for (Folder child : children) {
            collectSubFoldersRecursively(child.getId(), result);
        }
    }
}
