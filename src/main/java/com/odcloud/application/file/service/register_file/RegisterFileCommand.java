package com.odcloud.application.file.service.register_file;

import java.util.List;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record RegisterFileCommand(
    Long folderId,
    List<MultipartFile> files
) {

}
