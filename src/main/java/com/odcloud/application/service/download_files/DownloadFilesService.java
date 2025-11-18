package com.odcloud.application.service.download_files;

import com.odcloud.adapter.out.file.FileResponse;
import com.odcloud.application.port.in.DownloadFilesUseCase;
import com.odcloud.application.port.out.FileInfoStoragePort;
import com.odcloud.application.port.out.FilePort;
import com.odcloud.domain.model.FileInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class DownloadFilesService implements DownloadFilesUseCase {

    private final FilePort filePort;
    private final FileInfoStoragePort fileStoragePort;

    @Override
    public DownloadFilesServiceResponse download(List<Long> fileIds) {
        List<FileInfo> files = fileStoragePort.findByIds(fileIds);
        FileResponse fileResponse = filePort.readFiles(files);
        return new DownloadFilesServiceResponse(fileResponse.resource(), fileResponse.headers());
    }
}
