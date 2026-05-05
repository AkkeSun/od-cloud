package com.odcloud.domain.model;

import com.odcloud.infrastructure.util.DateUtil;
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
    private Long fileSize;
    private LocalDateTime regDt;

    public static FileHistory ofRename(FileInfo fileAfter, String beforeFileName, String actorEmail) {
        return FileHistory.builder()
            .fileId(fileAfter.getId())
            .groupId(fileAfter.getGroupId())
            .actionType(FileHistoryActionType.RENAME)
            .actorEmail(actorEmail)
            .beforeFileName(beforeFileName)
            .afterFileName(fileAfter.getFileName())
            .beforeFolderId(fileAfter.getFolderId())
            .afterFolderId(fileAfter.getFolderId())
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
            .fileSize(fileAfter.getFileSize())
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
            .fileSize(file.getFileSize())
            .regDt(LocalDateTime.now())
            .build();
    }

    public String getRegDtString() {
        return DateUtil.formatDateTime(regDt);
    }
}
