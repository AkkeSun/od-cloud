package com.odcloud.application.file.port.out.dto;

import lombok.Builder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

@Builder
public record FileResponse(
    Resource resource,
    HttpHeaders headers
) {

}
