package com.odcloud.application.file.service.upload_group_to_drive;

import com.odcloud.application.file.port.in.UploadGroupToDriveUseCase;
import com.odcloud.application.file.port.out.FileInfoStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.application.file.port.out.GoogleDrivePort;
import com.odcloud.application.file.port.out.dto.FileResponse;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class UploadGroupToDriveService implements UploadGroupToDriveUseCase {

    private final GroupStoragePort groupStoragePort;
    private final FileInfoStoragePort fileInfoStoragePort;
    private final FolderInfoStoragePort folderInfoStoragePort;
    private final FilePort filePort;
    private final GoogleDrivePort googleDrivePort;

    @Override
    public UploadGroupToDriveResponse upload(Long groupId) {
        Group group = groupStoragePort.findById(groupId);

        String groupFolderId = group.getDriveFolderId();
        if (groupFolderId == null) {
            groupFolderId = googleDrivePort.ensureFolder(group.getName());
            groupStoragePort.updateDriveFolderId(groupId, groupFolderId);
        }

        List<FileInfo> files = fileInfoStoragePort.findByGroupId(groupId);

        int totalFiles = files.size();
        int uploadedCount = 0;
        int skippedCount = 0;
        int failedCount = 0;

        Map<Long, String> subFolderIdCache = new HashMap<>();
        String resolvedGroupFolderId = groupFolderId;

        for (FileInfo file : files) {
            String targetFolderId = resolveTargetFolder(file.getFolderId(), resolvedGroupFolderId, subFolderIdCache);
            if (targetFolderId == null) {
                failedCount++;
                continue;
            }

            try {
                if (googleDrivePort.fileExists(targetFolderId, file.getFileName())) {
                    log.info("[UploadGroupToDriveService] 동일 파일명 존재 - skip: folderId={}, fileName={}",
                        targetFolderId, file.getFileName());
                    skippedCount++;
                    continue;
                }

                FileResponse fileResponse = filePort.readFile(file);
                try (InputStream inputStream = fileResponse.resource().getInputStream()) {
                    googleDrivePort.uploadFile(
                        targetFolderId,
                        file.getFileName(),
                        inputStream,
                        file.getFileSize() != null ? file.getFileSize() : 0L
                    );
                }
                uploadedCount++;

            } catch (IOException e) {
                log.warn("[UploadGroupToDriveService] 파일 스트림 처리 실패 - fileId={}, error={}",
                    file.getId(), e.getMessage());
                failedCount++;
            } catch (Exception e) {
                log.warn("[UploadGroupToDriveService] 파일 업로드 실패 - fileId={}, error={}",
                    file.getId(), e.getMessage());
                failedCount++;
            }
        }

        return UploadGroupToDriveResponse.builder()
            .totalFiles(totalFiles)
            .uploadedCount(uploadedCount)
            .skippedCount(skippedCount)
            .failedCount(failedCount)
            .build();
    }

    private String resolveTargetFolder(Long appFolderId, String groupFolderId,
        Map<Long, String> subFolderIdCache) {
        if (appFolderId == null) {
            return groupFolderId;
        }

        if (subFolderIdCache.containsKey(appFolderId)) {
            return subFolderIdCache.get(appFolderId);
        }

        subFolderIdCache.put(appFolderId, null);

        try {
            FolderInfo folderInfo = folderInfoStoragePort.findById(appFolderId);

            String parentDriveFolderId = resolveTargetFolder(
                folderInfo.getParentId(), groupFolderId, subFolderIdCache
            );

            String subFolderDriveId = googleDrivePort.ensureSubFolder(
                parentDriveFolderId, folderInfo.getName()
            );
            subFolderIdCache.put(appFolderId, subFolderDriveId);
            return subFolderDriveId;

        } catch (Exception e) {
            log.warn("[UploadGroupToDriveService] Drive 서브폴더 생성/조회 실패 - folderId={}, error={}",
                appFolderId, e.getMessage());
            return null;
        }
    }
}
