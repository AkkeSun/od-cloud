package com.odcloud.application.service.register_account;

public record RegisterAccountServiceResponse(
    Boolean result
) {

    public static RegisterAccountServiceResponse ofSuccess() {
        return new RegisterAccountServiceResponse(Boolean.TRUE);
    }
}
