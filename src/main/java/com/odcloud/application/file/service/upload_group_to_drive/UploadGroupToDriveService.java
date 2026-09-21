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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class UploadGroupToDriveService implements UploadGroupToDriveUseCase {

    // Drive API 계정당 호출 제한을 고려한 동시 업로드 수
    private static final int UPLOAD_CONCURRENCY = 4;

    private final GroupStoragePort groupStoragePort;
    private final FileInfoStoragePort fileInfoStoragePort;
    private final FolderInfoStoragePort folderInfoStoragePort;
    private final FilePort filePort;
    private final GoogleDrivePort googleDrivePort;

    private enum UploadResult {UPLOADED, SKIPPED, FAILED}

    @Override
    public UploadGroupToDriveResponse upload(Long groupId) {
        Group group = groupStoragePort.findById(groupId);

        String groupFolderId = group.getDriveFolderId();
        if (groupFolderId == null) {
            groupFolderId = googleDrivePort.ensureFolder(group.getName());
            groupStoragePort.updateDriveFolderId(groupId, groupFolderId);
        }

        List<FileInfo> files = fileInfoStoragePort.findByGroupId(groupId);

        // Drive 내 폴더 생성
        Map<Long, String> subFolderIdCache = new HashMap<>();
        String resolvedGroupFolderId = groupFolderId;
        files.stream()
            .map(FileInfo::getFolderId)
            .filter(Objects::nonNull)
            .distinct()
            .forEach(folderId ->
                resolveTargetFolder(folderId, resolvedGroupFolderId, subFolderIdCache));

        // 파일 업로드 병령 처리
        List<UploadResult> results = uploadAll(files, resolvedGroupFolderId, subFolderIdCache);

        return UploadGroupToDriveResponse.builder()
            .totalFiles(files.size())
            .uploadedCount(count(results, UploadResult.UPLOADED))
            .skippedCount(count(results, UploadResult.SKIPPED))
            .failedCount(count(results, UploadResult.FAILED))
            .build();
    }

    private List<UploadResult> uploadAll(
        List<FileInfo> files, String groupFolderId, Map<Long, String> subFolderIdCache
    ) {
        if (files.isEmpty()) {
            return List.of();
        }
        
        try (ExecutorService executor = Executors.newFixedThreadPool(
            Math.min(UPLOAD_CONCURRENCY, files.size()))
        ) {
            List<CompletableFuture<UploadResult>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> {
                    String targetFolderId = file.getFolderId() == null
                        ? groupFolderId
                        : subFolderIdCache.get(file.getFolderId());
                    return uploadFile(file, targetFolderId);
                }, executor))
                .toList();
            return futures.stream().map(CompletableFuture::join).toList();
        }
    }

    private UploadResult uploadFile(FileInfo file, String targetFolderId) {
        if (targetFolderId == null) {
            return UploadResult.FAILED;
        }

        try {
            if (googleDrivePort.fileExists(targetFolderId, file.getFileName())) {
                log.info("[UploadGroupToDriveService] 동일 파일명 존재 - skip: folderId={}, fileName={}",
                    targetFolderId, file.getFileName());
                return UploadResult.SKIPPED;
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
            return UploadResult.UPLOADED;

        } catch (IOException e) {
            log.warn("[UploadGroupToDriveService] 파일 스트림 처리 실패 - fileId={}, error={}",
                file.getId(), e.getMessage());
            return UploadResult.FAILED;
        } catch (Exception e) {
            log.warn("[UploadGroupToDriveService] 파일 업로드 실패 - fileId={}, error={}",
                file.getId(), e.getMessage());
            return UploadResult.FAILED;
        }
    }

    private int count(List<UploadResult> results, UploadResult target) {
        return (int) results.stream().filter(result -> result == target).count();
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

            if (folderInfo.getParentId() == null) {
                subFolderIdCache.put(appFolderId, groupFolderId);
                return groupFolderId;
            }

            String parentDriveFolderId = resolveTargetFolder(folderInfo.getParentId(),
                groupFolderId, subFolderIdCache);
            if (parentDriveFolderId == null) {
                return null;
            }

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
