package com.odcloud.application.service.download_files;

import lombok.Builder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

@Builder
public record DownloadFilesServiceResponse(
    Resource resource,
    HttpHeaders headers
) {

}
