package com.odcloud.application.file.service.download_file;

import com.odcloud.adapter.out.file.FileResponse;
import lombok.Builder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

@Builder
public record DownloadFileResponse(
    Resource resource,
    HttpHeaders headers
) {

    public static DownloadFileResponse of(FileResponse fileResponse) {
        return DownloadFileResponse.builder()
            .resource(fileResponse.resource())
            .headers(fileResponse.headers())
            .build();
    }
}
