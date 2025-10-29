package com.odcloud.adapter.out.persistence.mail;

import lombok.Builder;
import org.springframework.core.io.Resource;

@Builder
public record Attachment(
    String fileName,
    Resource resource
) {

}
