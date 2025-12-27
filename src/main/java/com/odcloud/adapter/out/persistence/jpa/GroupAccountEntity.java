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
@Table(name = "GROUP_ACCOUNT")
class GroupAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "GROUP_ID")
    private String groupId;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "MEMO")
    private String memo;

    @Column(name = "SHOW_YN")
    private String showYn;

    @Column(name = "MOD_DT")
    private LocalDateTime modDt;

    @Column(name = "REG_DT")
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
