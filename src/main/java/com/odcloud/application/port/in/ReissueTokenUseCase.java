package com.odcloud.application.port.in;

import com.odcloud.application.service.reissue_token.ReissueTokenServiceResponse;

public interface ReissueTokenUseCase {

    ReissueTokenServiceResponse reissueToken(String refreshToken);
}
