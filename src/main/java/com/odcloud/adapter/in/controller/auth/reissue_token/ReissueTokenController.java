package com.odcloud.adapter.in.controller.auth.reissue_token;

import com.odcloud.application.auth.port.in.ReissueTokenUseCase;
import com.odcloud.application.auth.service.reissue_token.ReissueTokenResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class ReissueTokenController {

    private final ReissueTokenUseCase useCase;

    @PutMapping("/auth")
    ApiResponse<ReissueTokenResponse> update(@RequestHeader String refreshToken) {
        return ApiResponse.ok(useCase.reissueToken(refreshToken));
    }
}
