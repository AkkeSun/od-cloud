package com.odcloud.application.util.port.out.dto;

import lombok.Builder;
import org.springframework.core.io.Resource;

@Builder
public record Attachment(
    String fileName,
    Resource resource
) {

}
