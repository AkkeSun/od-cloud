package com.odcloud.domain.model;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import com.odcloud.infrastructure.util.StringUtil;
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
    private String name;
    private String ownerEmail;
    private List<GroupAccount> groupMembers;
    private LocalDateTime regDt;

    public Group(String id, String ownerEmail, String name, LocalDateTime regDt) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.name = name;
        this.regDt = regDt;
    }

    public static Group of(RegisterGroupCommand command) {
        return Group.builder()
            .id(StringUtil.generateRandomString(6))
            .name(command.name())
            .ownerEmail(command.ownerEmail())
            .regDt(LocalDateTime.now())
            .build();
    }

    public static Group of(String name, String ownerEmail) {
        return Group.builder()
            .id(StringUtil.generateRandomString(6))
            .name(name)
            .ownerEmail(ownerEmail)
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
