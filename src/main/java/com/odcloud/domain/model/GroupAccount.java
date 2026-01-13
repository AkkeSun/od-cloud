package com.odcloud.domain.model;

import com.odcloud.application.group.port.in.command.UpdateGroupAccountStatusCommand;
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
    private Long groupId;
    private Long accountId;
    private String groupName;
    private String groupOwner;
    private String name;
    private String nickName;
    private String email;
    private String picture;
    private String status;
    private String memo;
    private String showYn;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public GroupAccount(Long id, Long groupId, Long accountId, String name, String nickName,
        String email, String picture, String status, String memo, LocalDateTime modDt,
        LocalDateTime regDt) {
        this.id = id;
        this.groupId = groupId;
        this.accountId = accountId;
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.picture = picture;
        this.status = status;
        this.memo = memo;
        this.modDt = modDt;
        this.regDt = regDt;
    }

    public static GroupAccount ofPending(Group group, Account account) {
        return GroupAccount.builder()
            .groupId(group.getId())
            .accountId(account.getId())
            .status("PENDING")
            .showYn("Y")
            .regDt(LocalDateTime.now())
            .build();
    }

    public static GroupAccount ofGroupOwner(Group group, Account account) {
        return GroupAccount.builder()
            .groupId(group.getId())
            .accountId(account.getId())
            .status("ACTIVE")
            .showYn("Y")
            .modDt(LocalDateTime.now())
            .regDt(LocalDateTime.now())
            .build();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateStatus(UpdateGroupAccountStatusCommand command) {
        this.status = command.status();
        this.memo = command.memo();
        this.modDt = LocalDateTime.now();
    }

    public void updateShowYn(String showYn) {
        this.showYn = showYn;
        this.modDt = LocalDateTime.now();
    }

    public boolean isBlock() {
        return "BLOCK".equals(status);
    }

    public boolean isActive() {
        return status.equals("ACTIVE");
    }

    public boolean isOwner(String email) {
        return groupOwner.equals(email);
    }
}
