package com.odcloud.application.file.service.register_file;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_STORAGE_LIMIT_EXCEEDED;

import com.odcloud.application.file.port.in.RegisterFileUseCase;
import com.odcloud.application.file.port.in.command.RegisterFileCommand;
import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
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
    private final ProfileConstant constant;
    private final FileInfoStoragePort fileStoragePort;
    private final FolderInfoStoragePort folderStoragePort;
    private final GroupStoragePort groupStoragePort;

    @Override
    @Transactional
    public RegisterFileServiceResponse register(RegisterFileCommand command) {
        FolderInfo folder = folderStoragePort.findById(command.folderId());

        long totalFileSize = 0;
        for (MultipartFile multipartFile : command.files()) {
            totalFileSize += multipartFile.getSize();
        }

        Group group = groupStoragePort.findById(folder.getGroupId());
        if (!group.canUpload(totalFileSize)) {
            throw new CustomBusinessException(Business_STORAGE_LIMIT_EXCEEDED);
        }

        for (MultipartFile multipartFile : command.files()) {
            FileInfo file = FileInfo.create(constant.fileUpload().diskPath(),
                folder, multipartFile);
            int fileNumber = 1;
            while (fileStoragePort.existsByFolderIdAndName(command.folderId(),
                file.getFileName())) {
                file.addFileNameNumber(++fileNumber);
            }

            fileStoragePort.save(file);
            filePort.uploadFile(file);
        }

        group.increaseStorageUsed(totalFileSize);
        groupStoragePort.save(group);

        return RegisterFileServiceResponse.ofSuccess();
    }
}