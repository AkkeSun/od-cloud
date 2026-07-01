package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.download_file.DownloadFileCommand;
import com.odcloud.application.file.service.download_file.DownloadFileResponse;

public interface DownloadFileUseCase {

    DownloadFileResponse downloadFile(DownloadFileCommand command);

}