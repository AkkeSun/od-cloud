package com.odcloud.application.file.service.backup_group_files;

import lombok.Builder;

@Builder
public record BackupGroupFilesResponse(
    int totalGroups,
    int successCount,
    int failCount,
    int skipCount
) {

}
