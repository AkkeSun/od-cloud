package com.odcloud.domain.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folder {

    private Long id;
    private Long parentId;
    private String groupId;
    private String name;
    private String owner;
    private String path;
    private String accessLevel;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public static Folder ofRootFolder(String groupId, String folderName, String owner) {
        return Folder.builder()
            .parentId(null)
            .groupId(groupId)
            .name(folderName)
            .owner(owner)
            .path("/" + groupId)
            .accessLevel("PUBLIC")
            .regDt(LocalDateTime.now())
            .build();
    }

    public static Folder createSubFolder(Long parentId, String parentPath, String groupId,
        String name, String accessLevel, String owner) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String folderName = uuid + "_" + date;

        return Folder.builder()
            .parentId(parentId)
            .groupId(groupId)
            .name(name)
            .owner(owner)
            .path(parentPath + "/" + folderName)
            .accessLevel(accessLevel)
            .regDt(LocalDateTime.now())
            .build();
    }
}
