package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.GroupAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "group_account")
class GroupAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "status")
    private String status;

    @Column(name = "memo")
    private String memo;

    @Column(name = "show_yn")
    private String showYn;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    static GroupAccountEntity of(GroupAccount groupAccount) {
        return GroupAccountEntity.builder()
            .id(groupAccount.getId())
            .groupId(groupAccount.getGroupId())
            .accountId(groupAccount.getAccountId())
            .status(groupAccount.getStatus())
            .memo(groupAccount.getMemo())
            .showYn(groupAccount.getShowYn())
            .modDt(groupAccount.getModDt())
            .regDt(groupAccount.getRegDt())
            .build();
    }
}
