package com.odcloud.application.service.register_file;

import com.odcloud.application.port.in.RegisterFileUseCase;
import com.odcloud.application.port.in.command.RegisterFileCommand;
import com.odcloud.application.port.out.FileInfoStoragePort;
import com.odcloud.application.port.out.FilePort;
import com.odcloud.application.port.out.FolderInfoStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
class RegisterFileService implements RegisterFileUseCase {

    private final FilePort filePort;
    private final FileInfoStoragePort fileStoragePort;
    private final FolderInfoStoragePort folderStoragePort;

    @Override
    @Transactional
    public RegisterFileServiceResponse register(RegisterFileCommand command) {
        FolderInfo folder = folderStoragePort.findById(command.folderId());

        for (MultipartFile multipartFile : command.files()) {
            FileInfo file = FileInfo.create(folder, multipartFile);
            int fileNumber = 1;
            while (fileStoragePort.existsByFolderIdAndName(command.folderId(),
                file.getFileName())) {
                file.addFileNameNumber(++fileNumber);
            }

            fileStoragePort.save(file);
            filePort.uploadFile(file);
        }

        return RegisterFileServiceResponse.ofSuccess();
    }
}