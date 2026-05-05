package com.odcloud.application.file.service.find_file_history;

import com.odcloud.domain.model.FileHistory;
import java.util.List;
import lombok.Builder;

@Builder
public record FindFileHistoryResponse(
    List<HistoryItem> histories
) {

    public static FindFileHistoryResponse of(List<FileHistory> histories) {
        return FindFileHistoryResponse.builder()
            .histories(histories.stream().map(HistoryItem::of).toList())
            .build();
    }

    @Builder
    public record HistoryItem(
        Long id,
        Long fileId,
        Long groupId,
        String actionType,
        String actorEmail,
        String beforeFileName,
        String afterFileName,
        Long beforeFolderId,
        Long afterFolderId,
        Long fileSize,
        String regDt
    ) {

        public static HistoryItem of(FileHistory history) {
            return HistoryItem.builder()
                .id(history.getId())
                .fileId(history.getFileId())
                .groupId(history.getGroupId())
                .actionType(history.getActionType().name())
                .actorEmail(history.getActorEmail())
                .beforeFileName(history.getBeforeFileName())
                .afterFileName(history.getAfterFileName())
                .beforeFolderId(history.getBeforeFolderId())
                .afterFolderId(history.getAfterFolderId())
                .fileSize(history.getFileSize())
                .regDt(history.getRegDtString())
                .build();
        }
    }
}
