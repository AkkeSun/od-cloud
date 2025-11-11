package com.odcloud.application.service.register_folder;


import com.odcloud.application.port.in.RegisterFolderUseCase;
import com.odcloud.application.port.in.command.RegisterFolderCommand;
import com.odcloud.application.port.out.FileUploadPort;
import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.domain.model.Folder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterFolderService implements RegisterFolderUseCase {

    private final FolderStoragePort folderStoragePort;
    private final FileUploadPort fileUploadPort;

    @Override
    @Transactional
    public RegisterFolderServiceResponse createFolder(RegisterFolderCommand command) {
        Folder parentFolder = folderStoragePort.findById(command.parentId());

        Folder folder = Folder.createSubFolder(command, parentFolder.getPath());
        folderStoragePort.save(folder);
        fileUploadPort.createFolder(folder.getPath());

        return RegisterFolderServiceResponse.ofSuccess();
    }
}
