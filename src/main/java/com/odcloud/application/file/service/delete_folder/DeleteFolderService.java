package com.odcloud.application.file.service.delete_folder;

import static com.odcloud.infrastructure.constant.CommonConstant.GROUP_LOCK;

import com.odcloud.application.auth.port.out.RedisStoragePort;
import com.odcloud.application.file.port.in.DeleteFolderUseCase;
import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
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
    private final GroupStoragePort groupStoragePort;
    private final FileInfoStoragePort fileInfoStoragePort;
    private final FolderInfoStoragePort folderInfoStoragePort;
    private final RedisStoragePort redisStoragePort;

    @Override
    @Transactional
    public DeleteFolderServiceResponse deleteFolder(Account account, Long folderId) {
        FolderInfo folder = folderInfoStoragePort.findById(folderId);
        if (!account.getGroupIds().contains(folder.getGroupId())) {
            throw new CustomAuthorizationException(ErrorCode.ACCESS_DENIED);
        }

        long deletedStorageSize = deleteFolderRecursively(folder, 0);
        if (deletedStorageSize > 0) {
            redisStoragePort.executeWithLock(GROUP_LOCK + folder.getGroupId(), () -> {
                Group group = groupStoragePort.findById(folder.getGroupId());
                group.decreaseStorageUsed(deletedStorageSize);
                groupStoragePort.save(group);
                return null;
            });
        }

        return DeleteFolderServiceResponse.ofSuccess();
    }

    private long deleteFolderRecursively(FolderInfo folder, long deletedStorageSize) {
        List<FolderInfo> childFolders = folderInfoStoragePort.findByParentId(folder.getId());
        for (FolderInfo childFolder : childFolders) {
            deletedStorageSize = deleteFolderRecursively(childFolder, deletedStorageSize);
        }

        List<FileInfo> files = fileInfoStoragePort.findByFolderId(folder.getId());
        for (FileInfo file : files) {
            filePort.deleteFile(file.getFileLoc());
            fileInfoStoragePort.delete(file);
            deletedStorageSize += file.getFileSize();
        }

        folderInfoStoragePort.delete(folder);
        return deletedStorageSize;
    }
}
