package com.odcloud.adapter.in.reissue_token;

import com.odcloud.application.port.in.ReissueTokenUseCase;
import com.odcloud.application.service.reissue_token.ReissueTokenServiceResponse;
import com.odcloud.infrastructure.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class ReissueTokenController {

    private final ReissueTokenUseCase useCase;

    @PutMapping("/auth")
    ApiResponse<ReissueTokenResponse> update(@RequestBody @Valid ReissueTokenRequest request) {
        ReissueTokenServiceResponse serviceResponse = useCase.reissueToken(request.refreshToken());

        return ApiResponse.ok(ReissueTokenResponse.of(serviceResponse));
    }
}
