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
public class File {

    private Long id;
    private Long folderId;
    private String fileName;
    private String fileLoc;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public static File create(Long folderId, String fileName, String fileLoc) {
        return File.builder()
            .folderId(folderId)
            .fileName(fileName)
            .fileLoc(fileLoc)
            .regDt(LocalDateTime.now())
            .build();
    }
}
