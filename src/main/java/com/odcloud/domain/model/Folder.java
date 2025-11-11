package com.odcloud.domain.model;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import java.time.LocalDateTime;
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

    public static Folder ofRootFolder(RegisterGroupCommand command) {
        return Folder.builder()
            .parentId(null)
            .groupId(command.id())
            .name(command.description())
            .owner(command.ownerEmail())
            .path("/" + command.id())
            .accessLevel("PUBLIC")
            .regDt(LocalDateTime.now())
            .build();
    }
}
