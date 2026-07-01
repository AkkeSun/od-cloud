package com.odcloud.application.file.service.download_files;

import java.util.List;
import lombok.Builder;

@Builder
public record DownloadFilesResponse(
    List<DownloadFileItem> files
) {

    @Builder
    public record DownloadFileItem(
        Long fileId,
        String fileName,
        String fileUrl
    ) {

    }
}
