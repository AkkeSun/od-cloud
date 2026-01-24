package com.odcloud.domain.model;

import com.odcloud.application.file.port.in.command.RegisterFolderCommand;
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
public class FolderInfo {

    private Long id;
    private Long parentId;
    private Long groupId;
    private String name;
    private String owner;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public static FolderInfo ofRootFolder(Group group) {
        return FolderInfo.builder()
            .parentId(null)
            .groupId(group.getId())
            .name(group.getName())
            .owner(group.getOwnerEmail())
            .regDt(LocalDateTime.now())
            .build();
    }


    public static FolderInfo createSubFolder(RegisterFolderCommand command) {
        return FolderInfo.builder()
            .parentId(command.parentId())
            .groupId(command.groupId())
            .name(command.name())
            .owner(command.owner())
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

    public void updateParentId(Long parentId) {
        this.parentId = parentId;
    }
}
