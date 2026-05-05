package com.odcloud.application.file.service.find_file_history;

import com.odcloud.application.file.port.in.FindFileHistoryUseCase;
import com.odcloud.application.file.port.out.FileHistoryStoragePort;
import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class FindFileHistoryService implements FindFileHistoryUseCase {

    private final FileHistoryStoragePort fileHistoryStoragePort;
    private final FileInfoStoragePort fileInfoStoragePort;

    @Override
    @Transactional(readOnly = true)
    public FindFileHistoryResponse findHistory(FindFileHistoryCommand command) {
        FileInfo file = fileInfoStoragePort.findById(command.fileId());
        if (!command.account().getGroupIds().contains(file.getGroupId())) {
            throw new CustomAuthorizationException(ErrorCode.ACCESS_DENIED);
        }
        return FindFileHistoryResponse.of(
            fileHistoryStoragePort.findByFileId(command.fileId())
        );
    }
}
