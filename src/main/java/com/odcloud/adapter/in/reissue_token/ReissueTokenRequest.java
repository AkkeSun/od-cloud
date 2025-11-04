package com.odcloud.adapter.in.reissue_token;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
record ReissueTokenRequest(
    @NotBlank(message = "리프레시 토큰은 필수값 입니다")
    String refreshToken
) {

}
