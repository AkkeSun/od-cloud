package com.odcloud.infrastructure.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.odcloud.domain.model.Account;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtUtil {

    String createTempToken(Account account);

    String createAccessToken(Account account);

    String createRefreshToken(Account account);

    boolean validateTokenExceptExpiration(String token);

    Claims getClaims(String token);

    ObjectNode getAccountInfo(HttpServletRequest request);
}

