package com.odcloud.application.file.service.upload_group_to_drive;

import lombok.Builder;

@Builder
public record UploadGroupToDriveResponse(
    int totalFiles,
    int uploadedCount,
    int skippedCount,
    int failedCount
) {

}
