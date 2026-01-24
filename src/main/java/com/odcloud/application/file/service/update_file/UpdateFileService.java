package com.odcloud.application.file.service.update_file;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_FOLDER;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_FILE_NAME;

import com.odcloud.application.file.port.in.UpdateFileUseCase;
import com.odcloud.application.file.port.in.command.UpdateFileCommand;
import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class UpdateFileService implements UpdateFileUseCase {

    private final FileInfoStoragePort fileStoragePort;
    private final FolderInfoStoragePort folderStoragePort;

    @Override
    @Transactional
    public UpdateFileServiceResponse update(UpdateFileCommand command) {
        FileInfo file = fileStoragePort.findById(command.fileId());

        if (command.isFileNameUpdate(file.getFileName())) {
            Long targetFolderId =
                command.folderId() != null ? command.folderId() : file.getFolderId();

            if (fileStoragePort.existsByFolderIdAndName(targetFolderId, command.fileName())) {
                throw new CustomBusinessException(Business_SAVED_FILE_NAME);
            }

            file.updateFileName(command.fileName());
        }

        if (command.isFileLocUpdate(file.getFolderId())) {
            if (!folderStoragePort.existsById(command.folderId())) {
                throw new CustomBusinessException(Business_DoesNotExists_FOLDER);
            }

            String targetFileName =
                command.fileName() != null ? command.fileName() : file.getFileName();
            if (fileStoragePort.existsByFolderIdAndName(command.folderId(), targetFileName)) {
                throw new CustomBusinessException(Business_SAVED_FILE_NAME);
            }

            file.updateFolderId(command.folderId());
        }

        fileStoragePort.save(file);
        return UpdateFileServiceResponse.ofSuccess();
    }
}
