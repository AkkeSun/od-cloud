package com.odcloud.application.file.service.download_file;

import com.odcloud.adapter.out.file.FileResponse;
import com.odcloud.application.file.port.in.DownloadFileUseCase;
import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.domain.model.FileInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class DownloadFileService implements DownloadFileUseCase {

    private final FilePort filePort;
    private final FileInfoStoragePort fileStoragePort;

    @Override
    public DownloadFileServiceResponse downloadFile(Long fileId) {
        FileInfo file = fileStoragePort.findById(fileId);
        FileResponse fileResponse = filePort.readFile(file);
        return DownloadFileServiceResponse.of(fileResponse);
    }
}
