package com.odcloud.adapter.in.create_file;

import com.odcloud.application.port.in.CreateFileUseCase;
import com.odcloud.application.port.in.command.CreateFileCommand;
import com.odcloud.infrastructure.api.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
class CreateFileController {

    private final CreateFileUseCase createFileUseCase;

    @PostMapping("/files")
    ApiResponse<CreateFileResponse> createFile(
        @RequestParam @NotNull(message = "폴더 ID는 필수값 입니다") Long folderId,
        @RequestParam @NotNull(message = "파일은 필수값 입니다") MultipartFile file) {

        CreateFileCommand command = CreateFileCommand.builder()
            .folderId(folderId)
            .file(file)
            .build();

        return ApiResponse.ofSuccess(CreateFileResponse.of(createFileUseCase.createFile(command)));
    }
}
