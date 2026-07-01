package com.odcloud.application.file.service.download_files;

import com.odcloud.application.file.port.in.DownloadFilesUseCase;
import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.application.file.service.download_files.DownloadFilesResponse.DownloadFileItem;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomAuthorizationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class DownloadFilesService implements DownloadFilesUseCase {

    private final FileInfoStoragePort fileStoragePort;
    private final ProfileConstant constant;

    @Override
    public DownloadFilesResponse download(DownloadFilesCommand command) {
        List<FileInfo> files = fileStoragePort.findByIds(command.fileIds());
        String webServerHost = constant.webServerHost();

        List<DownloadFileItem> items = files.stream()
            .map(file -> {
                if (!command.account().getGroupIds().contains(file.getGroupId())) {
                    throw new CustomAuthorizationException(ErrorCode.ACCESS_DENIED);
                }
                return DownloadFileItem.builder()
                    .fileId(file.getId())
                    .fileName(file.getFileName())
                    .fileUrl(webServerHost + file.getFileLoc())
                    .build();
            })
            .toList();

        return DownloadFilesResponse.builder()
            .files(items)
            .build();
    }
}
