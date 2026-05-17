package com.odcloud.adapter.in.controller.auth.issue_token;

import com.odcloud.application.auth.port.in.IssueTokenUseCase;
import com.odcloud.application.auth.service.issue_token.IssueTokenResponse;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class IssueTokenController {

    private final IssueTokenUseCase useCase;
    private final ProfileConstant constant;

    @PostMapping("/auth")
    ApiResponse<Boolean> issue(
        @RequestHeader String googleAuthorization,
        @RequestBody IssueTokenRequest request,
        HttpServletResponse response
    ) {
        IssueTokenResponse tokenResponse = useCase.issue(googleAuthorization, request.deviceId());
        setTokenCookies(response, tokenResponse.accessToken(), tokenResponse.refreshToken());
        return ApiResponse.ok(true);
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
