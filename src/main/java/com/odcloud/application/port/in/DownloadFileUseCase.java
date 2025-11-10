package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.DownloadFileCommand;
import com.odcloud.application.service.download_file.DownloadFileServiceResponse;

public interface DownloadFileUseCase {

    DownloadFileServiceResponse downloadFile(DownloadFileCommand command);
}
