package com.odcloud.adapter.in.controller.auth.reissue_token;

import com.odcloud.application.auth.port.in.ReissueTokenUseCase;
import com.odcloud.application.auth.service.reissue_token.ReissueTokenResponse;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class ReissueTokenController {

    private final ReissueTokenUseCase useCase;
    private final ProfileConstant constant;

    @PutMapping("/auth")
    ApiResponse<Boolean> update(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractTokenFromCookie(request);
        ReissueTokenResponse tokenResponse = useCase.reissueToken(refreshToken);
        setTokenCookies(response, tokenResponse.accessToken(), tokenResponse.refreshToken());
        return ApiResponse.ok(true);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
            .filter(c -> "refreshToken".equals(c.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
    }

    private void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        long accessMaxAge = constant.getAccessTokenTtl() / 1000;
        long refreshMaxAge = constant.getRefreshTokenTtl() / 1000;

        response.addHeader(HttpHeaders.SET_COOKIE,
            ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(accessMaxAge)
                .build().toString());

        response.addHeader(HttpHeaders.SET_COOKIE,
            ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(refreshMaxAge)
                .build().toString());
    }
}
