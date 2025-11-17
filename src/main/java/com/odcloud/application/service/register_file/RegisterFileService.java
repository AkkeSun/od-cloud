package com.odcloud.application.service.register_file;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_FILE_NAME;

import com.odcloud.application.port.in.RegisterFileUseCase;
import com.odcloud.application.port.in.command.RegisterFileCommand;
import com.odcloud.application.port.out.FilePort;
import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.application.port.out.FolderStoragePort;
import com.odcloud.domain.model.File;
import com.odcloud.domain.model.Folder;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.util.ArrayList;
import java.util.List;
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
    private final FileStoragePort fileStoragePort;
    private final FolderStoragePort folderStoragePort;

    @Override
    @Transactional
    public RegisterFileServiceResponse register(RegisterFileCommand command) {
        Folder folder = folderStoragePort.findById(command.folderId());

        List<String> savedFileNames = new ArrayList<>();
        for (MultipartFile multipartFile : command.files()) {
            File file = File.create(folder, multipartFile);
            if (fileStoragePort.existsByFolderIdAndName(command.folderId(), file.getFileName())) {
                filePort.deleteFiles(savedFileNames);
                throw new CustomBusinessException(Business_SAVED_FILE_NAME);
            }

            fileStoragePort.save(file);
            filePort.uploadFile(file);
            savedFileNames.add(file.getFileName());
        }

        return RegisterFileServiceResponse.ofSuccess();
    }
}