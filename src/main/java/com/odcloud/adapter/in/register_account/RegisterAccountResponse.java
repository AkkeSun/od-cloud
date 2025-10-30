package com.odcloud.adapter.in.register_account;

import com.odcloud.application.service.register_account.RegisterAccountServiceResponse;
import lombok.Builder;

@Builder
record RegisterAccountResponse(
    Boolean result,
    String optUrl
) {

    static RegisterAccountResponse of(RegisterAccountServiceResponse serviceResponse) {
        return RegisterAccountResponse.builder()
            .result(serviceResponse.result())
            .optUrl(serviceResponse.otpUrl())
            .build();
    }
}
