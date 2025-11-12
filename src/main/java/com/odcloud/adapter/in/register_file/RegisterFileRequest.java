package com.odcloud.adapter.in.register_file;

import com.odcloud.application.port.in.command.RegisterFileCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "폴더 ID는 필수값 입니다")
    private Long folderId;

    @NotEmpty(message = "파일은 필수값 입니다")
    private List<MultipartFile> files;

    RegisterFileCommand toCommand() {
        return RegisterFileCommand.builder()
            .folderId(folderId)
            .files(files)
            .build();
    }
}
