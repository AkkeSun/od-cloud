package com.odcloud.application.file.service.backup_group_files;

import com.odcloud.application.file.port.in.BackupGroupFilesUseCase;
import com.odcloud.application.file.port.out.FileHistoryStoragePort;
import com.odcloud.application.file.port.out.FilePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.application.file.port.out.GoogleDrivePort;
import com.odcloud.application.file.port.out.dto.FileResponse;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.domain.model.FileHistory;
import com.odcloud.domain.model.FileHistoryActionType;
import com.odcloud.domain.model.FileInfo;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class BackupGroupFilesService implements BackupGroupFilesUseCase {

    private final GroupStoragePort groupStoragePort;
    private final FileHistoryStoragePort fileHistoryStoragePort;
    private final FilePort filePort;
    private final GoogleDrivePort googleDrivePort;
    private final FolderInfoStoragePort folderInfoStoragePort;

    @Override
    public BackupGroupFilesResponse backup() {
        List<Group> groups = groupStoragePort.findAllEnabledForBackup();

        int successCount = 0;
        int failCount = 0;
        int skipCount = 0;

        for (Group group : groups) {
            List<FileHistory> pendingHistories =
                fileHistoryStoragePort.findByGroupIdAndBackupDtIsNull(group.getId());

            if (pendingHistories.isEmpty()) {
                skipCount++;
                continue;
            }

            String groupFolderId = group.getDriveFolderId();
            if (groupFolderId == null) {
                try {
                    String folderName = group.getName();
                    groupFolderId = googleDrivePort.ensureFolder(folderName);
                    groupStoragePort.updateDriveFolderId(group.getId(), groupFolderId);
                } catch (Exception e) {
                    log.error(
                        "[BackupGroupFilesService] Drive 그룹 폴더 생성/조회 실패 - groupId={}, error={}",
                        group.getId(), e.getMessage());
                    failCount++;
                    continue;
                }
            }

            // 백업 실행 중 서브폴더 ID 캐시 (같은 folderId 반복 조회 방지)
            Map<Long, String> subFolderIdCache = new HashMap<>();

            List<Long> backupIds = new ArrayList<>();
            for (FileHistory history : pendingHistories) {
                if (history.getActionType() == FileHistoryActionType.DELETE) {
                    if (history.getBeforeFolderId() != null && history.getBeforeFileName() != null) {
                        String oldDriveFolderId = resolveTargetFolder(
                            history.getBeforeFolderId(), groupFolderId, subFolderIdCache
                        );
                        if (oldDriveFolderId != null) {
                            try {
                                googleDrivePort.deleteFile(oldDriveFolderId, history.getBeforeFileName());
                            } catch (Exception e) {
                                log.warn("[BackupGroupFilesService] Drive 파일 삭제 실패 - historyId={}, error={}",
                                    history.getId(), e.getMessage());
                                failCount++;
                                // continue 제거: 삭제 실패해도 이력은 처리 완료로 기록하여 중복 재처리 방지
                            }
                        }
                    }
                    backupIds.add(history.getId());
                    continue;
                }

                if (history.getFileLoc() == null || history.getAfterFileName() == null) {
                    log.warn("[BackupGroupFilesService] 이력 데이터 이상 - historyId={}, actionType={}",
                        history.getId(), history.getActionType());
                    failCount++;
                    continue;
                }

                String targetFolderId = resolveTargetFolder(
                    history.getAfterFolderId(), groupFolderId, subFolderIdCache
                );
                if (targetFolderId == null) {
                    failCount++;
                    continue;
                }

                try {
                    FileInfo fileInfo = FileInfo.builder()
                        .fileLoc(history.getFileLoc())
                        .fileName(history.getAfterFileName())
                        .fileSize(history.getFileSize())
                        .build();

                    FileResponse fileResponse = filePort.readFile(fileInfo);
                    try (InputStream inputStream = fileResponse.resource().getInputStream()) {
                        googleDrivePort.uploadFile(
                            targetFolderId,
                            history.getAfterFileName(),
                            inputStream,
                            history.getFileSize() != null ? history.getFileSize() : 0L
                        );
                    }
                    backupIds.add(history.getId());

                    // MOVE/RENAME: 이전 위치의 Drive 파일 삭제
                    if ((history.getActionType() == FileHistoryActionType.MOVE
                        || history.getActionType() == FileHistoryActionType.RENAME)
                        && history.getBeforeFolderId() != null
                        && history.getBeforeFileName() != null) {
                        String oldDriveFolderId = resolveTargetFolder(
                            history.getBeforeFolderId(), groupFolderId, subFolderIdCache
                        );
                        if (oldDriveFolderId != null) {
                            try {
                                googleDrivePort.deleteFile(oldDriveFolderId, history.getBeforeFileName());
                            } catch (Exception e) {
                                log.warn("[BackupGroupFilesService] Drive 이전 폴더 파일 삭제 실패 - historyId={}, error={}",
                                    history.getId(), e.getMessage());
                            }
                        }
                    }
                } catch (IOException e) {
                    log.warn("[BackupGroupFilesService] 파일 스트림 처리 실패 - historyId={}, error={}",
                        history.getId(), e.getMessage());
                    failCount++;
                } catch (Exception e) {
                    log.warn("[BackupGroupFilesService] 파일 백업 실패 - historyId={}, error={}",
                        history.getId(), e.getMessage());
                    failCount++;
                }
            }

            if (!backupIds.isEmpty()) {
                fileHistoryStoragePort.updateBackupDt(backupIds, LocalDateTime.now());
                successCount++;
            }
        }

        return BackupGroupFilesResponse.builder()
            .totalGroups(groups.size())
            .successCount(successCount)
            .failCount(failCount)
            .skipCount(skipCount)
            .build();
    }

    // parentId 체인을 재귀적으로 따라가며 Drive 폴더 계층을 그대로 재현한다.
    // parentId == null 이면 그룹 Drive 루트 폴더에 대응한다.
    // 런타임 캐시로 같은 폴더에 대한 Drive API 중복 호출을 방지한다.
    private String resolveTargetFolder(Long appFolderId, String groupFolderId,
        Map<Long, String> subFolderIdCache) {
        if (appFolderId == null) {
            return groupFolderId;
        }

        if (subFolderIdCache.containsKey(appFolderId)) {
            // sentinel(null)이 등록된 경우 순환 참조 감지 → null 반환
            return subFolderIdCache.get(appFolderId);
        }

        // 순환 참조 방어: 처리 시작 전 sentinel(null) 등록
        subFolderIdCache.put(appFolderId, null);

        try {
            FolderInfo folderInfo = folderInfoStoragePort.findById(appFolderId);

            if (folderInfo.getParentId() == null) {
                subFolderIdCache.put(appFolderId, groupFolderId);
                return groupFolderId;
            }

            String parentDriveFolderId = resolveTargetFolder(
                folderInfo.getParentId(), groupFolderId, subFolderIdCache
            );

            String subFolderName = folderInfo.getName();
            String subFolderDriveId = googleDrivePort.ensureSubFolder(parentDriveFolderId,
                subFolderName);
            subFolderIdCache.put(appFolderId, subFolderDriveId);  // sentinel → 실제 Drive ID로 교체
            return subFolderDriveId;

        } catch (Exception e) {
            log.warn("[BackupGroupFilesService] Drive 서브폴더 생성/조회 실패 - folderId={}, error={}",
                appFolderId, e.getMessage());
            return null;
        }
    }
}
