package com.odcloud.infrastructure.util;

import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.constant.ProfileConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
        Claims claims = Jwts.claims().setSubject(account.getUsername());
        return "Bearer " + Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + constant.jwt().ttl()))
            .signWith(SignatureAlgorithm.HS256, constant.jwt().secretKey())
            .compact();
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
            return Jwts.parser().setSigningKey(constant.jwt().secretKey())
                .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            // todo
            throw new RuntimeException(e);
        }
    }
}
