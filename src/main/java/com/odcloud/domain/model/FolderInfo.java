package com.odcloud.domain.model;

import com.odcloud.application.file.port.in.command.RegisterFolderCommand;
import com.odcloud.infrastructure.util.DateUtil;
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
public class FolderInfo {

    private Long id;
    private Long parentId;
    private Long groupId;
    private String name;
    private String owner;
    private String path;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public static FolderInfo ofRootFolder(Group group) {
        return FolderInfo.builder()
            .parentId(null)
            .groupId(group.getId())
            .name(group.getName())
            .owner(group.getOwnerEmail())
            .path("/" + group.getId())
            .regDt(LocalDateTime.now())
            .build();
    }


    public static FolderInfo createSubFolder(RegisterFolderCommand command, String parentPath) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String folderName = uuid + "_" + date;

        return FolderInfo.builder()
            .parentId(command.parentId())
            .groupId(command.groupId())
            .name(command.name())
            .owner(command.owner())
            .path(parentPath + "/" + folderName)
            .regDt(LocalDateTime.now())
            .build();
    }

    public String getRegDtString() {
        return DateUtil.formatDateTime(regDt);
    }

    public void update(String name) {
        if (name != null) {
            this.name = name;
        }
        this.modDt = LocalDateTime.now();
    }

    public void updateWithNewPath(String name, String newPath) {
        if (name != null) {
            this.name = name;
        }
        this.path = newPath;
        this.modDt = LocalDateTime.now();
    }

    public void updateParentId(Long parentId) {
        this.parentId = parentId;
    }
}
