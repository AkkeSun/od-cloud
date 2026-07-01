package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.download_files.DownloadFilesCommand;
import com.odcloud.application.file.service.download_files.DownloadFilesResponse;

public interface DownloadFilesUseCase {

    DownloadFilesResponse download(DownloadFilesCommand command);
}
