package com.odcloud.application.service.register_account;

import lombok.Builder;

@Builder
public record RegisterAccountServiceResponse(
    Boolean result,
    String otpUrl
) {

    public static RegisterAccountServiceResponse of(String otpAuthUrl) {
        return RegisterAccountServiceResponse.builder()
            .result(true)
            .otpUrl(otpAuthUrl)
            .build();
    }
}
