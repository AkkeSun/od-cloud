package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Account;
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
@Table(name = "TBL_ACCOUNT")
class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "TWO_FACTOR_SECRET")
    private String twoFactorSecret;

    @Column(name = "IS_ADMIN_APPROVED")
    private Boolean isAdminApproved;

    @Column(name = "REG_DATE_TIME")
    private LocalDateTime regDt;

    static AccountEntity of(Account account) {
        return AccountEntity.builder()
            .id(account.getId())
            .username(account.getUsername())
            .password(account.getPassword())
            .email(account.getEmail())
            .role(account.getRole())
            .twoFactorSecret(account.getTwoFactorSecret())
            .isAdminApproved(account.getIsAdminApproved())
            .regDt(account.getRegDt())
            .build();
    }

    Account toDomain() {
        return Account.builder()
            .id(id)
            .username(username)
            .password(password)
            .email(email)
            .role(role)
            .twoFactorSecret(twoFactorSecret)
            .isAdminApproved(isAdminApproved)
            .regDt(regDt)
            .build();
    }
}
