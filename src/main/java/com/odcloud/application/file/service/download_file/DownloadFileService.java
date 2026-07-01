package com.odcloud.application.file.service.download_file;

import com.odcloud.application.file.port.in.DownloadFileUseCase;
import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class DownloadFileService implements DownloadFileUseCase {

    private final FileInfoStoragePort fileStoragePort;
    private final ProfileConstant constant;

    @Override
    public DownloadFileResponse downloadFile(DownloadFileCommand command) {
        FileInfo file = fileStoragePort.findById(command.fileId());
        if (!command.account().getGroupIds().contains(file.getGroupId())) {
            throw new CustomAuthorizationException(ErrorCode.ACCESS_DENIED);
        }

        return DownloadFileResponse.of(file, constant.webServerHost());
    }
}
