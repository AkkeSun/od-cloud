package com.odcloud.fakeClass;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeJwtUtil implements JwtUtil {

    public String mockAccessToken = "fake-access-token";
    public String mockRefreshToken = "fake-refresh-token";
    public String mockEmail = "test@example.com";
    public String mockDeviceId = "fake-device-id";
    public boolean shouldReturnInvalidToken = false;

    @Override
    public String createAccessToken(Account account) {
        log.info("FakeJwtUtil createAccessToken: email={}", account.getEmail());
        return mockAccessToken + "-" + account.getEmail();
    }

    @Override
    public String createRefreshToken(Account account, String deviceId) {
        log.info("FakeJwtUtil createRefreshToken: email={}, deviceId={}", account.getEmail(), deviceId);
        return mockRefreshToken + "-" + account.getEmail();
    }

    @Override
    public String getDeviceId(String token) {
        return mockDeviceId;
    }

    @Override
    public String getEmail(String token) {
        if (token.contains("-")) {
            String[] parts = token.split("-");
            return parts[parts.length - 1];
        }
        return mockEmail;
    }

    @Override
    public boolean validateTokenExceptExpiration(String token) {
        if (shouldReturnInvalidToken) {
            return false;
        }
        return token != null && !token.isEmpty();
    }

    @Override
    public Claims getClaims(String token) {
        return Jwts.claims();
    }

    @Override
    public ObjectNode getAccountInfo(HttpServletRequest request) {
        return null;
    }

    public void reset() {
        mockAccessToken = "fake-access-token";
        mockRefreshToken = "fake-refresh-token";
        mockEmail = "test@example.com";
        mockDeviceId = "fake-device-id";
        shouldReturnInvalidToken = false;
    }
}
