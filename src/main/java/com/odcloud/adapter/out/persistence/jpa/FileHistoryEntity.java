package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.FileHistory;
import com.odcloud.domain.model.FileHistoryActionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file_history",
    indexes = {
        @Index(name = "idx_file_history_file_id", columnList = "file_id"),
        @Index(name = "idx_file_history_group_id", columnList = "group_id"),
        @Index(name = "idx_file_history_file_reg", columnList = "file_id, reg_dt"),
        @Index(name = "idx_file_history_group_reg", columnList = "group_id, reg_dt")
    }
)
class FileHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 20)
    private FileHistoryActionType actionType;

    @Column(name = "actor_email")
    private String actorEmail;

    @Column(name = "before_file_name")
    private String beforeFileName;

    @Column(name = "after_file_name")
    private String afterFileName;

    @Column(name = "before_folder_id")
    private Long beforeFolderId;

    @Column(name = "after_folder_id")
    private Long afterFolderId;

    @Column(name = "file_loc")
    private String fileLoc;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "backup_dt")
    private LocalDateTime backupDt;

    @Column(name = "reg_dt", nullable = false)
    private LocalDateTime regDt;

    static FileHistoryEntity of(FileHistory history) {
        return FileHistoryEntity.builder()
            .fileId(history.getFileId())
            .groupId(history.getGroupId())
            .actionType(history.getActionType())
            .actorEmail(history.getActorEmail())
            .beforeFileName(history.getBeforeFileName())
            .afterFileName(history.getAfterFileName())
            .beforeFolderId(history.getBeforeFolderId())
            .afterFolderId(history.getAfterFolderId())
            .fileLoc(history.getFileLoc())
            .fileSize(history.getFileSize())
            .backupDt(history.getBackupDt())
            .regDt(history.getRegDt())
            .build();
    }
}
