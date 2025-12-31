package com.odcloud.application.auth.service.reissue_token;

import lombok.Builder;

@Builder
public record ReissueTokenServiceResponse(
    String accessToken,
    String refreshToken
) {

}
