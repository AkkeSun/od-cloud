package com.odcloud.application.file.service.delete_file;

import com.odcloud.application.file.port.in.DeleteFileUseCase;
import com.odcloud.application.file.port.in.command.DeleteFileCommand;
import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class DeleteFileService implements DeleteFileUseCase {

    private final FilePort filePort;
    private final FileInfoStoragePort fileInfoStoragePort;
    private final GroupStoragePort groupStoragePort;

    @Override
    @Transactional
    public DeleteFileServiceResponse deleteFile(DeleteFileCommand command) {
        List<DeleteFileServiceResponseItem> logs = new ArrayList<>();
        boolean allSuccess = true;

        for (Long fileId : command.fileIds()) {
            try {
                FileInfo file = fileInfoStoragePort.findById(fileId);
                if (!command.account().getGroupIds().contains(file.getGroupId())) {
                    throw new CustomAuthorizationException(ErrorCode.ACCESS_DENIED);
                }

                filePort.deleteFile(file.getFileLoc());
                fileInfoStoragePort.delete(file);

                Group group = groupStoragePort.findById(file.getGroupId());
                group.decreaseStorageUsed(file.getFileSize());
                groupStoragePort.save(group);

                logs.add(DeleteFileServiceResponseItem.ofSuccess(fileId));
            } catch (Exception e) {
                allSuccess = false;
                logs.add(DeleteFileServiceResponseItem.builder()
                    .fileId(fileId)
                    .errorMessage(e.getMessage())
                    .build());
            }
        }

        return DeleteFileServiceResponse.builder()
            .result(allSuccess)
            .logs(logs)
            .build();
    }
}
