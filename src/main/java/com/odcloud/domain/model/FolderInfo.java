package com.odcloud.domain.model;

import com.odcloud.application.port.in.command.RegisterFolderCommand;
import com.odcloud.application.port.in.command.RegisterGroupCommand;
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
    private String groupId;
    private String name;
    private String owner;
    private String path;
    private String accessLevel;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public static FolderInfo ofRootFolder(RegisterGroupCommand command) {
        return FolderInfo.builder()
            .parentId(null)
            .groupId(command.id())
            .name(command.description())
            .owner(command.ownerEmail())
            .path("/" + command.id())
            .accessLevel("PUBLIC")
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
            .accessLevel(command.accessLevel())
            .regDt(LocalDateTime.now())
            .build();
    }

    public String getRegDtString() {
        return DateUtil.formatDateTime(regDt);
    }
}
