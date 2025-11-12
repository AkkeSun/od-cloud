package com.odcloud.application.service.register_file;

import com.odcloud.application.port.in.RegisterFileUseCase;
import com.odcloud.application.port.in.command.RegisterFileCommand;
import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.application.port.out.FileUploadPort;
import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.domain.model.File;
import com.odcloud.domain.model.Folder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
class RegisterFileService implements RegisterFileUseCase {

    private final FileUploadPort fileUploadPort;
    private final FileStoragePort fileStoragePort;
    private final FolderStoragePort folderStoragePort;

    @Override
    @Transactional
    public RegisterFileServiceResponse register(RegisterFileCommand command) {
        Folder folder = folderStoragePort.findById(command.folderId());
        for (MultipartFile multipartFile : command.files()) {
            File file = File.create(folder, multipartFile);
            fileStoragePort.save(file);
            fileUploadPort.uploadFile(file);
        }

        return RegisterFileServiceResponse.ofSuccess();
    }
}