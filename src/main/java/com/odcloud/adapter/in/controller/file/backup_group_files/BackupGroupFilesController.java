package com.odcloud.adapter.in.controller.file.backup_group_files;

import com.odcloud.application.file.port.in.BackupGroupFilesUseCase;
import com.odcloud.application.file.service.backup_group_files.BackupGroupFilesResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class BackupGroupFilesController {

    private final BackupGroupFilesUseCase useCase;

    @PostMapping("/files/backup")
    ApiResponse<BackupGroupFilesResponse> backup() {
        return ApiResponse.ok(useCase.backup());
    }
}
