package com.odcloud.application.file.service.download_file;

import com.odcloud.adapter.out.file.FileResponse;
import lombok.Builder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

@Builder
public record DownloadFileServiceResponse(
    Resource resource,
    HttpHeaders headers
) {

    public static DownloadFileServiceResponse of(FileResponse fileResponse) {
        return DownloadFileServiceResponse.builder()
            .resource(fileResponse.resource())
            .headers(fileResponse.headers())
            .build();
    }
}
