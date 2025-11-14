package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.DownloadFileCommand;
import com.odcloud.application.port.in.command.DownloadFilesCommand;
import com.odcloud.application.port.in.command.DownloadFolderCommand;
import com.odcloud.application.service.download_file.DownloadFileServiceResponse;

public interface DownloadFileUseCase {

    /**
     * 단건 파일 다운로드
     */
    DownloadFileServiceResponse downloadFile(DownloadFileCommand command);

    /**
     * 복수 파일 다운로드 (압축)
     */
    DownloadFileServiceResponse downloadFiles(DownloadFilesCommand command);

    /**
     * 폴더 다운로드 (압축)
     */
    DownloadFileServiceResponse downloadFolder(DownloadFolderCommand command);
}
