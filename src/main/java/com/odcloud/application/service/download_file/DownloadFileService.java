package com.odcloud.application.service.download_file;

import com.odcloud.adapter.out.file.FileResponse;
import com.odcloud.application.port.in.DownloadFileUseCase;
import com.odcloud.application.port.out.FilePort;
import com.odcloud.application.port.out.FileStoragePort;
import com.odcloud.domain.model.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class DownloadFileService implements DownloadFileUseCase {

    private final FilePort filePort;
    private final FileStoragePort fileStoragePort;

    @Override
    public DownloadFileServiceResponse downloadFile(Long fileId) {
        File file = fileStoragePort.findById(fileId);
        FileResponse fileResponse = filePort.readFile(file);
        return DownloadFileServiceResponse.of(fileResponse);
    }
}
