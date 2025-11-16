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
public class GroupAccount {

    private Long id;
    private String groupId;
    private Long accountId;
    private String groupName;
    private String name;
    private String nickName;
    private String email;
    private String status;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public GroupAccount(Long id, String groupId, Long accountId, String name, String nickName,
        String email, String status, LocalDateTime modDt, LocalDateTime regDt) {
        this.id = id;
        this.groupId = groupId;
        this.accountId = accountId;
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.status = status;
        this.modDt = modDt;
        this.regDt = regDt;
    }


    public static GroupAccount of(Group group, Account account) {
        return GroupAccount.builder()
            .groupId(group.getId())
            .accountId(account.getId())
            .status("PENDING")
            .regDt(LocalDateTime.now())
            .build();
    }

    public static GroupAccount ofGroupOwner(Group group, Account account) {
        return GroupAccount.builder()
            .groupId(group.getId())
            .accountId(account.getId())
            .status("ACTIVE")
            .modDt(LocalDateTime.now())
            .regDt(LocalDateTime.now())
            .build();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateStatus(String status) {
        this.status = status;
        this.modDt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status.equals("ACTIVE");
    }
}
