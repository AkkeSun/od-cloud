package com.odcloud.application.service.register_file;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_FILE_NAME;

import com.odcloud.application.port.in.RegisterFileUseCase;
import com.odcloud.application.port.in.command.RegisterFileCommand;
import com.odcloud.application.port.out.FileInfoStoragePort;
import com.odcloud.application.port.out.FilePort;
import com.odcloud.application.port.out.FolderInfoStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
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
    private final FileInfoStoragePort fileStoragePort;
    private final FolderInfoStoragePort folderStoragePort;

    @Override
    @Transactional
    public RegisterFileServiceResponse register(RegisterFileCommand command) {
        FolderInfo folder = folderStoragePort.findById(command.folderId());

        List<String> savedFileNames = new ArrayList<>();
        for (MultipartFile multipartFile : command.files()) {
            FileInfo file = FileInfo.create(folder, multipartFile);
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