package com.odcloud.adapter.in.controller.file.upload_group_to_drive;

import com.odcloud.application.file.port.in.UploadGroupToDriveUseCase;
import com.odcloud.application.file.service.upload_group_to_drive.UploadGroupToDriveResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UploadGroupToDriveController {

    private final UploadGroupToDriveUseCase useCase;

    @GetMapping("/groups/{groupId}/drive/upload")
    ApiResponse<UploadGroupToDriveResponse> upload(@PathVariable Long groupId) {
        return ApiResponse.ok(useCase.upload(groupId));
    }
}
