package com.odcloud.application.port.in.command;

import java.util.List;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record RegisterFileCommand(Long folderId, List<MultipartFile> files) {

}
