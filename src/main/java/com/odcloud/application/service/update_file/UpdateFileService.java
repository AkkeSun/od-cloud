package com.odcloud.application.service.update_file;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_FILE_NAME;

import com.odcloud.application.port.in.UpdateFileUseCase;
import com.odcloud.application.port.in.command.UpdateFileCommand;
import com.odcloud.application.port.out.FileInfoStoragePort;
import com.odcloud.application.port.out.FilePort;
import com.odcloud.application.port.out.FolderInfoStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
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
    private final FilePort filePort;

    @Override
    @Transactional
    public UpdateFileServiceResponse update(UpdateFileCommand command) {
        FileInfo file = fileStoragePort.findById(command.fileId());

        // 파일명 변경
        if (command.fileName() != null && !command.fileName().equals(file.getFileName())) {
            Long targetFolderId =
                command.folderId() != null ? command.folderId() : file.getFolderId();

            if (fileStoragePort.existsByFolderIdAndName(targetFolderId, command.fileName())) {
                throw new CustomBusinessException(Business_SAVED_FILE_NAME);
            }

            file.updateFileName(command.fileName());
        }

        // 폴더 이동
        if (command.folderId() != null && !command.folderId().equals(file.getFolderId())) {
            FolderInfo targetFolder = folderStoragePort.findById(command.folderId());

            // 대상 폴더에 동일한 파일명이 있는지 확인
            String targetFileName =
                command.fileName() != null ? command.fileName() : file.getFileName();
            if (fileStoragePort.existsByFolderIdAndName(command.folderId(), targetFileName)) {
                throw new CustomBusinessException(Business_SAVED_FILE_NAME);
            }

            // 새로운 파일 경로 생성
            String newFileLoc = generateNewFileLoc(targetFolder, file.getFileName());

            // 실제 파일 이동
            filePort.moveFile(file.getFileLoc(), newFileLoc);

            // DB 업데이트
            file.updateFolder(command.folderId(), newFileLoc);
        }

        fileStoragePort.save(file);

        return UpdateFileServiceResponse.ofSuccess();
    }

    private String generateNewFileLoc(FolderInfo targetFolder, String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String serverFileName = uuid + "_" + date + extension;

        return targetFolder.getPath() + "/" + serverFileName;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
