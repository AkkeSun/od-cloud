package com.odcloud.infrastructure.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtUtil {

    String createTempToken(String username);

    boolean validateTokenExceptExpiration(String token);

    Claims getClaims(String token);

    ObjectNode getAccountInfo(HttpServletRequest request);
}

