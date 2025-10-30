package com.odcloud.domain.model;

import com.odcloud.application.port.in.command.RegisterAccountCommand;
import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
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
    private String username;
    private String password;
    private String email;
    private String role;
    private String twoFactorSecret;
    private Boolean isAdminApproved;
    private LocalDateTime regDt;

    public static Account of(Claims claims) {
        Object role = claims.get("role");
        Object id = claims.get("id");
        return Account.builder()
            .username(claims.getSubject())
            .role(role == null ? null : role.toString())
            .email(id == null ? null : id.toString())
            .build();
    }

    public static Account of(RegisterAccountCommand command, String twoFactorSecret) {
        return Account.builder()
            .username(command.username())
            .password(command.password())
            .email(command.email())
            .role(command.role())
            .twoFactorSecret(twoFactorSecret)
            .isAdminApproved(Boolean.FALSE)
            .regDt(LocalDateTime.now())
            .build();
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public Boolean isAdminApproved() {
        return isAdminApproved;
    }
}
