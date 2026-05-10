package com.odcloud.application.file.port.in;

import com.odcloud.application.file.service.upload_group_to_drive.UploadGroupToDriveResponse;

public interface UploadGroupToDriveUseCase {

    UploadGroupToDriveResponse upload(Long groupId);
}
