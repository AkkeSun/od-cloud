package com.odcloud.adapter.in.reissue_token;

import com.odcloud.application.port.in.ReissueTokenUseCase;
import com.odcloud.application.service.reissue_token.ReissueTokenServiceResponse;
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
        ReissueTokenServiceResponse serviceResponse = useCase.reissueToken(refreshToken);
        return ApiResponse.ok(ReissueTokenResponse.of(serviceResponse));
    }
}
