package com.odcloud.domain.model;

import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;
import com.odcloud.application.port.in.command.RegisterAccountCommand;
import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private Long id;
    private String email;
    private String nickname;
    private String name;
    private String picture;
    private List<Group> groups;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public static Account of(Claims claims) {
        List<String> groupIds = (List<String>) claims.get("groups");
        return Account.builder()
            .email(claims.getSubject())
            .id(((Number) claims.get("id")).longValue())
            .nickname(claims.get("nickname").toString())
            .picture(claims.get("picture").toString())
            .groups(groupIds.stream().map(Group::of).collect(Collectors.toList()))
            .build();
    }

    public static Account of(GoogleUserInfoResponse userInfo, RegisterAccountCommand command) {
        return Account.builder()
            .email(userInfo.email())
            .nickname(userInfo.name())
            .name(command.name())
            .picture(userInfo.picture())
            .groups(List.of(Group.of(command.groupId())))
            .regDt(LocalDateTime.now())
            .build();
    }

    public List<String> getGroupIds() {
        return groups.stream()
            .map(Group::getId)
            .collect(Collectors.toList());
    }

    public void updateGroups(List<Group> groups) {
        this.groups = groups;
    }
}
