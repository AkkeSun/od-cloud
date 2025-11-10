package com.odcloud.application.service.create_folder;

import com.odcloud.application.port.in.CreateFolderUseCase;
import com.odcloud.application.port.in.command.CreateFolderCommand;
import com.odcloud.application.port.out.FileUploadPort;
import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.domain.model.Folder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class CreateFolderService implements CreateFolderUseCase {

    private final FolderStoragePort folderStoragePort;
    private final FileUploadPort fileUploadPort;

    @Override
    @Transactional
    public CreateFolderServiceResponse createFolder(CreateFolderCommand command) {
        Folder parentFolder = folderStoragePort.findById(command.parentId());

        Folder folder = Folder.createSubFolder(
            command.parentId(),
            parentFolder.getPath(),
            command.groupId(),
            command.name(),
            command.accessLevel(),
            command.owner()
        );

        folderStoragePort.save(folder);
        fileUploadPort.createFolder(folder.getPath());

        return CreateFolderServiceResponse.ofSuccess();
    }
}
