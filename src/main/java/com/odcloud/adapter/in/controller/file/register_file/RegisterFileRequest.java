package com.odcloud.adapter.in.controller.file.register_file;

import com.odcloud.application.port.in.command.RegisterFileCommand;
import com.odcloud.infrastructure.util.StringUtil;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RegisterFileRequest {

    @NotEmpty(message = "파일은 필수값 입니다")
    private List<MultipartFile> files;

    RegisterFileCommand toCommand(Long folderId) {
        return RegisterFileCommand.builder()
            .folderId(folderId)
            .files(files)
            .build();
    }

    @Override
    public String toString() {
        return StringUtil.toString(this);
    }
}
