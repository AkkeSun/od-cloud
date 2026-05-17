package com.odcloud.adapter.in.controller.auth.reissue_token;

import com.odcloud.application.auth.port.in.ReissueTokenUseCase;
import com.odcloud.application.auth.service.reissue_token.ReissueTokenResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import com.odcloud.infrastructure.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class ReissueTokenController {

    private final ReissueTokenUseCase useCase;
    private final CookieUtil cookieUtil;

    @PutMapping("/auth")
    ApiResponse<AccessTokenResponse> update(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getRefreshToken(request);
        ReissueTokenResponse tokenResponse = useCase.reissueToken(refreshToken);
        cookieUtil.setRefreshTokenCookie(response, tokenResponse.refreshToken());
        return ApiResponse.ok(new AccessTokenResponse(tokenResponse.accessToken()));
    }
}
