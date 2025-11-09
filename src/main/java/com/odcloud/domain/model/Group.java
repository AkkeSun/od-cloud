package com.odcloud.domain.model;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    private String id;
    private String ownerEmail;
    private String description;
    private List<GroupAccount> groupMembers;
    private LocalDateTime regDt;

    public Group(String id, String ownerEmail, String description, LocalDateTime regDt) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.description = description;
        this.regDt = regDt;
    }

    public static Group of(RegisterGroupCommand command) {
        return Group.builder()
            .id(command.id())
            .ownerEmail(command.ownerEmail())
            .description(command.description())
            .regDt(LocalDateTime.now())
            .build();
    }

    public static Group of(String id) {
        return Group.builder()
            .id(id)
            .build();
    }

    public void updateGroupMembers(List<GroupAccount> groupMembers) {
        this.groupMembers = groupMembers;
    }
}
