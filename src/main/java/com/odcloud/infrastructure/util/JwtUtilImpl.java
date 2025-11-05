package com.odcloud.infrastructure.util;

import static com.odcloud.infrastructure.exception.ErrorCode.INVALID_GOOGLE_TOKEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.auth.openidconnect.IdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtilImpl implements JwtUtil {

    private final ProfileConstant constant;

    @Override
    public String createTempToken(Account account) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(account.getUsername());
        return "Bearer " + Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + constant.getTempTokenTtl()))
            .signWith(SignatureAlgorithm.HS256, constant.getJwtSecretKey())
            .compact();
    }

    @Override
    public String createAccessToken(Account account) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(account.getUsername());
        claims.put("id", account.getId());
        claims.put("role", account.getRole());
        return "Bearer " + Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + constant.getAccessTokenTtl()))
            .signWith(SignatureAlgorithm.HS256, constant.getJwtSecretKey())
            .compact();
    }

    @Override
    public String createRefreshToken(Account account) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(account.getUsername());
        return "Bearer " + Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + constant.getRefreshTokenTtl()))
            .signWith(SignatureAlgorithm.HS256, constant.getJwtSecretKey())
            .compact();
    }

    @Override
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    @Override
    public boolean validateTokenExceptExpiration(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Claims getClaims(String token) {
        try {
            token = token.replace("Bearer ", "");
            return Jwts.parser().setSigningKey(constant.getJwtSecretKey())
                .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new CustomAuthenticationException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    @Override
    public Payload getGooglePayload(String token) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory())
                .setAudience(Collections.singletonList(constant.googleOAuth2().clientId()))
                .build();

            return verifier.verify(token).getPayload();
        } catch (Exception e) {
            throw new CustomAuthenticationException(INVALID_GOOGLE_TOKEN);
        }
    }

    @Override
    public ObjectNode getAccountInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null) {
            return new ObjectMapper().createObjectNode();
        }

        ObjectNode userInfo = new ObjectMapper().createObjectNode();
        try {
            Claims claims = getClaims(token);
            userInfo.put("username", claims.getSubject());
            return userInfo;
        } catch (Exception e) {
            return new ObjectMapper().createObjectNode();
        }
    }
}
