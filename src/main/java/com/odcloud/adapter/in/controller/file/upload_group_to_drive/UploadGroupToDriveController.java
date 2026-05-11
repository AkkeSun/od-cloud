package com.odcloud.adapter.in.controller.file.upload_group_to_drive;

import com.odcloud.application.file.port.in.UploadGroupToDriveUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UploadGroupToDriveController {

    private final UploadGroupToDriveUseCase useCase;

    @GetMapping("/groups/{groupId}/drive/upload")
    ResponseEntity<Void> upload(@PathVariable Long groupId) {
        useCase.upload(groupId);
        return ResponseEntity.ok().build();
    }
}
