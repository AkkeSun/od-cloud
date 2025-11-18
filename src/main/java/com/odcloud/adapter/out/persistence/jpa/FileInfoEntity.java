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
@Table(name = "FILE_INFO",
    indexes = {
        @Index(name = "idx_file_name_fulltext", columnList = "FILE_NAME")
    }
)
class FileInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FOLDER_ID")
    private Long folderId;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_LOC")
    private String fileLoc;

    @Column(name = "MOD_DT")
    private LocalDateTime modDt;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    static FileInfoEntity of(FileInfo file) {
        return FileInfoEntity.builder()
            .id(file.getId())
            .folderId(file.getFolderId())
            .fileName(file.getFileName())
            .fileLoc(file.getFileLoc())
            .modDt(file.getModDt())
            .regDt(file.getRegDt())
            .build();
    }
}
