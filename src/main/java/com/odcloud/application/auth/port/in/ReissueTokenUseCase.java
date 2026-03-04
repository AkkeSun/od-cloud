package com.odcloud.application.auth.port.in;

import com.odcloud.application.auth.service.reissue_token.ReissueTokenResponse;

public interface ReissueTokenUseCase {

    ReissueTokenResponse reissueToken(String refreshToken);
}
