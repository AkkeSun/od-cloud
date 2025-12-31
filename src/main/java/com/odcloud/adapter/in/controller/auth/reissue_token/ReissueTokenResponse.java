package com.odcloud.adapter.in.controller.auth.reissue_token;

import com.odcloud.application.auth.service.reissue_token.ReissueTokenServiceResponse;
import lombok.Builder;

@Builder
record ReissueTokenResponse(
    String accessToken,
    String refreshToken
) {

    static ReissueTokenResponse of(ReissueTokenServiceResponse serviceResponse) {
        return ReissueTokenResponse.builder()
            .accessToken(serviceResponse.accessToken())
            .refreshToken(serviceResponse.refreshToken())
            .build();
    }
}
