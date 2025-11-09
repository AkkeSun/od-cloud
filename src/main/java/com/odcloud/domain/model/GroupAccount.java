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
    private String name;
    private String nickName;
    private String email;
    private String status;
    private LocalDateTime updateDt;
    private LocalDateTime regDt;

    public static GroupAccount of(Group group, Account account) {
        return GroupAccount.builder()
            .groupId(group.getId())
            .accountId(account.getId())
            .status("PENDING")
            .regDt(LocalDateTime.now())
            .build();
    }

    public void updateName(String name) {
        this.name = name;
    }
}
