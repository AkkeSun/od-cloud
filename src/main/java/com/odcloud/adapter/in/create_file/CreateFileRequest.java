package com.odcloud.adapter.in.create_file;

import com.odcloud.application.port.in.command.CreateFileCommand;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

record CreateFileRequest(@NotNull(message = "폴더 ID는 필수값 입니다") Long folderId,
    @NotNull(message = "파일은 필수값 입니다") MultipartFile file) {

    CreateFileCommand toCommand() {
        return CreateFileCommand.builder()
            .folderId(folderId)
            .file(file)
            .build();
    }
}
