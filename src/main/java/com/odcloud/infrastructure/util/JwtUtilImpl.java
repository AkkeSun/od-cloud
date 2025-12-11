package com.odcloud.infrastructure.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomAuthenticationException;
import com.odcloud.infrastructure.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtilImpl implements JwtUtil {

    private final ProfileConstant constant;
    
    @Override
    public String createAccessToken(Account account) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(account.getEmail());
        claims.put("id", account.getId());
        claims.put("groups", account.getGroupsInfo());
        claims.put("nickname", account.getNickname());
        claims.put("picture", account.getPicture());
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
        Claims claims = Jwts.claims().setSubject(account.getEmail());
        return "Bearer " + Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + constant.getRefreshTokenTtl()))
            .signWith(SignatureAlgorithm.HS256, constant.getJwtSecretKey())
            .compact();
    }

    @Override
    public String getEmail(String token) {
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
    public ObjectNode getAccountInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null) {
            return new ObjectMapper().createObjectNode();
        }

        ObjectNode userInfo = new ObjectMapper().createObjectNode();
        try {
            Claims claims = getClaims(token);
            userInfo.put("email", claims.getSubject());
            return userInfo;
        } catch (Exception e) {
            return new ObjectMapper().createObjectNode();
        }
    }
}
