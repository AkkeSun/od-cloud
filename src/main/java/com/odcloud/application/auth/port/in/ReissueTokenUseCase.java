package com.odcloud.application.auth.port.in;

import com.odcloud.application.auth.service.reissue_token.ReissueTokenServiceResponse;

public interface ReissueTokenUseCase {

    ReissueTokenServiceResponse reissueToken(String refreshToken);
}
