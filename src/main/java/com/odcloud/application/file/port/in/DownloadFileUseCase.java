package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.download_file.DownloadFileServiceResponse;

public interface DownloadFileUseCase {

    DownloadFileServiceResponse downloadFile(Long fileId);
}
