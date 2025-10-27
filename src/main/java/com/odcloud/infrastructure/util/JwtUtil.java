package com.odcloud.infrastructure.util;

import com.odcloud.domain.model.Account;
import io.jsonwebtoken.Claims;

public interface JwtUtil {

    String createAccessToken(Account account);

    boolean validateTokenExceptExpiration(String token);

    Claims getClaims(String token);
}

