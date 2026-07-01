package com.odcloud.application.file.service.download_file;

import com.odcloud.domain.model.FileInfo;
import lombok.Builder;

@Builder
public record DownloadFileResponse(
    String fileName,
    String fileUrl
) {

    public static DownloadFileResponse of(FileInfo file, String webServerHost) {
        return DownloadFileResponse.builder()
            .fileName(file.getFileName())
            .fileUrl(webServerHost + file.getFileLoc())
            .build();
    }
}
