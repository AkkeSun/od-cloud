package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.download_file.DownloadFileResponse;
import com.odcloud.application.file.service.find_files.FindFilesCommand;
import com.odcloud.application.file.service.find_files.FindFilesResponse;

public interface FindFilesUseCase {

    FindFilesResponse findAll(FindFilesCommand command);

    interface DownloadFileUseCase {

        DownloadFileResponse downloadFile(Long fileId);
    }
}
