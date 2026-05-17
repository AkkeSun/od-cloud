package com.odcloud.infrastructure.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import com.odcloud.infrastructure.constant.ProfileConstant;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final ProfileConstant constant;

    public String getAccessToken(HttpServletRequest request) {
        return getCookieValue(request, "accessToken");
    }

    public String getRefreshToken(HttpServletRequest request) {
        return getCookieValue(request, "refreshToken");
    }

    public void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        boolean isSecure = "prod".equals(constant.profile());
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie("accessToken", accessToken,
            constant.getAccessTokenTtl() / 1000, isSecure));
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie("refreshToken", refreshToken,
            constant.getRefreshTokenTtl() / 1000, isSecure));
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
            .filter(c -> name.equals(c.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
    }

    private String buildCookie(String name, String value, long maxAge, boolean secure) {
        return ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(secure)
            .path("/")
            .sameSite("Lax")
            .maxAge(maxAge)
            .build()
            .toString();
    }
}
