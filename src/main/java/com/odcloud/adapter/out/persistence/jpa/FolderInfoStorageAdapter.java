package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_FOLDER;

import com.odcloud.application.file.port.in.command.FindFilesCommand;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class FolderInfoStorageAdapter implements FolderInfoStoragePort {

    private final FolderInfoRepository folderRepository;

    @Override
    public void save(FolderInfo folder) {
        folderRepository.save(folder);
    }

    @Override
    public FolderInfo findById(Long id) {
        return folderRepository.findById(id).orElseThrow(
            () -> new CustomBusinessException(Business_DoesNotExists_FOLDER));
    }

    @Override
    public List<FolderInfo> findAll(FindFilesCommand command) {
        return folderRepository.findAll(command);
    }

    @Override
    public boolean existsSameFolderName(Long parentId, String name) {
        return folderRepository.existsSameFolderName(parentId, name);
    }

    @Override
    public List<FolderInfo> findByParentId(Long parentId) {
        return folderRepository.findByParentId(parentId);
    }

    @Override
    public void delete(FolderInfo folder) {
        folderRepository.delete(folder);
    }

    @Override
    public FolderInfo findRootFolderByGroupId(String groupId) {
        return folderRepository.findRootFolderByGroupId(groupId).orElseThrow(
            () -> new CustomBusinessException(Business_DoesNotExists_FOLDER));
    }
}
