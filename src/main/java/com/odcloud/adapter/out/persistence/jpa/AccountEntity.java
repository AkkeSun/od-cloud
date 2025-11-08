package com.odcloud.adapter.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBL_ACCOUNT")
class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "NICK_NAME")
    private String nickname;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PICTURE")
    private String picture;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ACCOUNT_GROUP",
        joinColumns = @JoinColumn(name = "ACCOUNT_ID"),
        inverseJoinColumns = @JoinColumn(name = "GROUP_ID")
    )
    @Column(name = "GROUP")
    private Set<GroupEntity> groups = new HashSet<>();

    @Column(name = "IS_ADMIN_APPROVED")
    private Boolean isAdminApproved;

    @Column(name = "UPDATE_DT")
    private LocalDateTime updateDt;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;
}
