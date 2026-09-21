package com.odcloud.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileHistory {

    private Long id;
    private Long fileId;
    private Long groupId;
    private FileHistoryActionType actionType;
    private String actorEmail;
    private String beforeFileName;
    private String afterFileName;
    private Long beforeFolderId;
    private Long afterFolderId;
    private String fileLoc;
    private Long fileSize;
    private LocalDateTime backupDt;
    private LocalDateTime regDt;

    public static FileHistory ofUpload(FileInfo file, String actorEmail) {
        return FileHistory.builder()
            .fileId(file.getId())
            .groupId(file.getGroupId())
            .actionType(FileHistoryActionType.UPLOAD)
            .actorEmail(actorEmail)
            .afterFileName(file.getFileName())
            .afterFolderId(file.getFolderId())
            .fileLoc(file.getFileLoc())
            .fileSize(file.getFileSize())
            .regDt(LocalDateTime.now())
            .build();
    }

    public static FileHistory ofRename(FileInfo fileAfter, String beforeFileName,
        String actorEmail) {
        return FileHistory.builder()
            .fileId(fileAfter.getId())
            .groupId(fileAfter.getGroupId())
            .actionType(FileHistoryActionType.RENAME)
            .actorEmail(actorEmail)
            .beforeFileName(beforeFileName)
            .afterFileName(fileAfter.getFileName())
            .beforeFolderId(fileAfter.getFolderId())
            .afterFolderId(fileAfter.getFolderId())
            .fileLoc(fileAfter.getFileLoc())
            .fileSize(fileAfter.getFileSize())
            .regDt(LocalDateTime.now())
            .build();
    }

    public static FileHistory ofMove(FileInfo fileAfter, Long beforeFolderId, String actorEmail) {
        return FileHistory.builder()
            .fileId(fileAfter.getId())
            .groupId(fileAfter.getGroupId())
            .actionType(FileHistoryActionType.MOVE)
            .actorEmail(actorEmail)
            .beforeFileName(fileAfter.getFileName())
            .afterFileName(fileAfter.getFileName())
            .beforeFolderId(beforeFolderId)
            .afterFolderId(fileAfter.getFolderId())
            .fileLoc(fileAfter.getFileLoc())
            .fileSize(fileAfter.getFileSize())
            .regDt(LocalDateTime.now())
            .build();
    }

    // 폴더 이름 변경/이동은 별도 테이블 없이 file_history를 재사용한다.
    // fileId 컬럼에는 folderId를, beforeFolderId/afterFolderId 컬럼에는 부모 폴더 id를 담는다.
    public static FileHistory ofFolderRename(FolderInfo folderAfter, String beforeName,
        String actorEmail) {
        return FileHistory.builder()
            .fileId(folderAfter.getId())
            .groupId(folderAfter.getGroupId())
            .actionType(FileHistoryActionType.FOLDER_RENAME)
            .actorEmail(actorEmail)
            .beforeFileName(beforeName)
            .afterFileName(folderAfter.getName())
            .beforeFolderId(folderAfter.getParentId())
            .afterFolderId(folderAfter.getParentId())
            .regDt(LocalDateTime.now())
            .build();
    }

    public static FileHistory ofFolderMove(FolderInfo folderAfter, Long beforeParentId,
        String actorEmail) {
        return FileHistory.builder()
            .fileId(folderAfter.getId())
            .groupId(folderAfter.getGroupId())
            .actionType(FileHistoryActionType.FOLDER_MOVE)
            .actorEmail(actorEmail)
            .beforeFileName(folderAfter.getName())
            .afterFileName(folderAfter.getName())
            .beforeFolderId(beforeParentId)
            .afterFolderId(folderAfter.getParentId())
            .regDt(LocalDateTime.now())
            .build();
    }

    public static FileHistory ofDelete(FileInfo file, String actorEmail) {
        return FileHistory.builder()
            .fileId(file.getId())
            .groupId(file.getGroupId())
            .actionType(FileHistoryActionType.DELETE)
            .actorEmail(actorEmail)
            .beforeFileName(file.getFileName())
            .beforeFolderId(file.getFolderId())
            .fileLoc(file.getFileLoc())
            .fileSize(file.getFileSize())
            .regDt(LocalDateTime.now())
            .build();
    }
}
