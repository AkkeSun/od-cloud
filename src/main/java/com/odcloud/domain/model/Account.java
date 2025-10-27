package com.odcloud.domain.model;

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

    private String id;
    private String username;
    private String password;
    private String role;
    private String twoFactorSecret;
    private boolean twoFactorEnabled;
    private LocalDateTime regDt;

    public static Account of(Claims claims) {
        return Account.builder()
            .username(claims.getSubject())
            .role(claims.get("role").toString())
            .build();
    }
}
