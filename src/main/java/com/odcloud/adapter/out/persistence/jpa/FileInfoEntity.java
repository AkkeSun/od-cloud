package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.FileInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "file_info",
    indexes = {
        @Index(name = "idx_file_name_fulltext", columnList = "file_name")
    }
)
class FileInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "folder_id")
    private Long folderId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_loc")
    private String fileLoc;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    static FileInfoEntity of(FileInfo file) {
        return FileInfoEntity.builder()
            .id(file.getId())
            .folderId(file.getFolderId())
            .fileName(file.getFileName())
            .fileLoc(file.getFileLoc())
            .fileSize(file.getFileSize())
            .modDt(file.getModDt())
            .regDt(file.getRegDt())
            .build();
    }
}
