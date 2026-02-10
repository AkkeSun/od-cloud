package com.odcloud.application.file.service.register_file;

import static com.odcloud.infrastructure.constant.CommonConstant.GROUP_LOCK;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_STORAGE_LIMIT_EXCEEDED;

import com.odcloud.application.auth.port.out.RedisStoragePort;
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
    private final RedisStoragePort redisStoragePort;

    @Override
    @Transactional
    public RegisterFileServiceResponse register(RegisterFileCommand command) {
        FolderInfo folder = folderStoragePort.findById(command.folderId());

        long totalFileSize = command.files().stream().mapToLong(MultipartFile::getSize).sum();
        redisStoragePort.executeWithLock(GROUP_LOCK + folder.getGroupId(), () -> {
            Group group = groupStoragePort.findById(folder.getGroupId());
            if (!group.canUpload(totalFileSize)) {
                throw new CustomBusinessException(Business_STORAGE_LIMIT_EXCEEDED);
            }
            group.increaseStorageUsed(totalFileSize);
            groupStoragePort.save(group);
            return null;
        });

        try {
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
        } catch (Exception e) {
            redisStoragePort.executeWithLock(GROUP_LOCK + folder.getGroupId(), () -> {
                Group group = groupStoragePort.findById(folder.getGroupId());
                group.decreaseStorageUsed(totalFileSize);
                groupStoragePort.save(group);
                return null;
            });
            throw e;
        }

        return RegisterFileServiceResponse.ofSuccess();
    }
}