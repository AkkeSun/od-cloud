package com.odcloud.application.service.update_folder;

import static com.odcloud.infrastructure.exception.ErrorCode.ACCESS_DENIED;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_FORBIDDEN_ACCESS;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_FOLDER_NAME;

import com.odcloud.application.port.in.UpdateFolderUseCase;
import com.odcloud.application.port.in.command.UpdateFolderCommand;
import com.odcloud.application.port.out.FilePort;
import com.odcloud.application.port.out.FolderInfoStoragePort;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UpdateFolderService implements UpdateFolderUseCase {

    private final FilePort filePort;
    private final FolderInfoStoragePort folderStoragePort;

    @Override
    @Transactional
    public UpdateFolderServiceResponse updateFolder(UpdateFolderCommand command) {
        FolderInfo folder = folderStoragePort.findById(command.folderId());
        if (!folder.getOwner().equals(command.account().getEmail())) {
            throw new CustomAuthorizationException(ACCESS_DENIED);
        }

        if (command.parentId() != null && !command.parentId().equals(folder.getParentId())) {
            handleParentFolderChange(folder, command);
        } else if (command.name() != null && !command.name().equals(folder.getName())) {
            handleNameChange(folder, command);
        } else {
            folder.update(command.name());
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

        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String folderName = uuid + "_" + date;
        String newPath = newParentFolder.getPath() + "/" + folderName;

        filePort.moveFolder(folder.getPath(), newPath);

        folder.updateWithNewPath(newName, newPath);
        folder.updateParentId(command.parentId());
    }

    private void handleNameChange(FolderInfo folder, UpdateFolderCommand command) {
        if (folderStoragePort.existsSameFolderName(folder.getParentId(), command.name())) {
            throw new CustomBusinessException(Business_SAVED_FOLDER_NAME);
        }
        folder.update(command.name());
    }
}
