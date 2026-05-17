package com.odcloud.adapter.in.controller.auth.issue_token;

import com.odcloud.application.auth.port.in.IssueTokenUseCase;
import com.odcloud.application.auth.service.issue_token.IssueTokenResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.infrastructure.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class IssueTokenController {

    private final IssueTokenUseCase useCase;
    private final CookieUtil cookieUtil;

    @PostMapping("/auth")
    ApiResponse<AccessTokenResponse> issue(
        @RequestHeader String googleAuthorization,
        @RequestBody IssueTokenRequest request,
        HttpServletResponse response
    ) {
        IssueTokenResponse tokenResponse = useCase.issue(googleAuthorization, request.deviceId());
        cookieUtil.setRefreshTokenCookie(response, tokenResponse.refreshToken());
        return ApiResponse.ok(new AccessTokenResponse(tokenResponse.accessToken()));
    }
}
