package com.odcloud.application.port.in.command;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record CreateFileCommand(Long folderId, MultipartFile file) {

}
