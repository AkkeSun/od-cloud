package com.odcloud.application.file.service.register_folder;


import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_FOLDER_NAME;

import com.odcloud.application.file.port.in.RegisterFolderUseCase;
import com.odcloud.application.file.port.in.command.RegisterFolderCommand;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterFolderService implements RegisterFolderUseCase {

    private final FolderInfoStoragePort folderStoragePort;
    private final FilePort fileUploadPort;

    @Override
    @Transactional
    public RegisterFolderServiceResponse createFolder(RegisterFolderCommand command) {
        FolderInfo parentFolder = folderStoragePort.findById(command.parentId());
        if (folderStoragePort.existsSameFolderName(command.parentId(), command.name())) {
            throw new CustomBusinessException(Business_SAVED_FOLDER_NAME);
        }

        FolderInfo folder = FolderInfo.createSubFolder(command, parentFolder.getPath());
        folderStoragePort.save(folder);
        fileUploadPort.createFolder(folder.getPath());

        return RegisterFolderServiceResponse.ofSuccess();
    }
}
