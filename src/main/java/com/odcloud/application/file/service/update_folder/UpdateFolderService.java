package com.odcloud.application.file.service.update_folder;

import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_FORBIDDEN_ACCESS;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_FOLDER_NAME;

import com.odcloud.application.file.port.in.UpdateFolderUseCase;
import com.odcloud.application.file.port.in.command.UpdateFolderCommand;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UpdateFolderService implements UpdateFolderUseCase {

    private final FolderInfoStoragePort folderStoragePort;

    @Override
    @Transactional
    public UpdateFolderServiceResponse updateFolder(UpdateFolderCommand command) {
        FolderInfo folder = folderStoragePort.findById(command.folderId());
        if (!command.account().getGroupIds().contains(folder.getGroupId())) {
            throw new CustomAuthorizationException(ACCESS_DENIED);
        }

        if (command.isFolderLocChange(folder.getParentId())) {
            handleParentFolderChange(folder, command);
        } else if (command.isFolderNameChange(folder.getName())) {
            handleNameChange(folder, command);
        }

        folderStoragePort.save(folder);
        return UpdateFolderServiceResponse.ofSuccess();
    }

    private void handleParentFolderChange(FolderInfo folder, UpdateFolderCommand command) {
        FolderInfo newParentFolder = folderStoragePort.findById(command.parentId());
        if (!newParentFolder.getGroupId().equals(folder.getGroupId())) {
            throw new CustomBusinessException(Business_FORBIDDEN_ACCESS);
        }

        String newName = command.name() != null ? command.name() : folder.getName();
        if (folderStoragePort.existsSameFolderName(command.parentId(), newName)) {
            throw new CustomBusinessException(Business_SAVED_FOLDER_NAME);
        }
        folder.updateParentId(command.parentId());
    }

    private void handleNameChange(FolderInfo folder, UpdateFolderCommand command) {
        if (folderStoragePort.existsSameFolderName(folder.getParentId(), command.name())) {
            throw new CustomBusinessException(Business_SAVED_FOLDER_NAME);
        }
        folder.update(command.name());
    }
}
