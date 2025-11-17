package com.odcloud.application.service.register_folder;


import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_FOLDER_NAME;

import com.odcloud.application.port.in.RegisterFolderUseCase;
import com.odcloud.application.port.in.command.RegisterFolderCommand;
import com.odcloud.application.port.out.FilePort;
import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.domain.model.Folder;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterFolderService implements RegisterFolderUseCase {

    private final FolderStoragePort folderStoragePort;
    private final FilePort fileUploadPort;

    @Override
    @Transactional
    public RegisterFolderServiceResponse createFolder(RegisterFolderCommand command) {
        Folder parentFolder = folderStoragePort.findById(command.parentId());
        if (folderStoragePort.existsSameFolderName(command.parentId(), command.name())) {
            throw new CustomBusinessException(Business_SAVED_FOLDER_NAME);
        }

        Folder folder = Folder.createSubFolder(command, parentFolder.getPath());
        folderStoragePort.save(folder);
        fileUploadPort.createFolder(folder.getPath());

        return RegisterFolderServiceResponse.ofSuccess();
    }
}
