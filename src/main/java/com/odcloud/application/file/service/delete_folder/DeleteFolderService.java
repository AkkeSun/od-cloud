package com.odcloud.application.file.service.delete_folder;

import com.odcloud.application.file.port.in.DeleteFolderUseCase;
import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class DeleteFolderService implements DeleteFolderUseCase {

    private final FilePort filePort;
    private final FileInfoStoragePort fileInfoStoragePort;
    private final FolderInfoStoragePort folderInfoStoragePort;

    @Override
    @Transactional
    public DeleteFolderServiceResponse deleteFolder(Account account, Long folderId) {
        FolderInfo folder = folderInfoStoragePort.findById(folderId);

        if (folder.getGroupId() != null) {
            if (!account.getGroupIds().contains(folder.getGroupId())) {
                throw new CustomAuthorizationException(ErrorCode.ACCESS_DENIED);
            }
        } else {
            if (!folder.getOwner().equals(account.getEmail())) {
                throw new CustomAuthorizationException(ErrorCode.ACCESS_DENIED);
            }
        }

        filePort.deleteFolder(folder.getPath());
        deleteFolderRecursivelyFromDB(folder);

        return DeleteFolderServiceResponse.ofSuccess();
    }

    private void deleteFolderRecursivelyFromDB(FolderInfo folder) {
        List<FolderInfo> childFolders = folderInfoStoragePort.findByParentId(folder.getId());
        for (FolderInfo childFolder : childFolders) {
            deleteFolderRecursivelyFromDB(childFolder);
        }
        List<FileInfo> files = fileInfoStoragePort.findByFolderId(folder.getId());
        for (FileInfo file : files) {
            fileInfoStoragePort.delete(file);
        }
        folderInfoStoragePort.delete(folder);
    }
}
