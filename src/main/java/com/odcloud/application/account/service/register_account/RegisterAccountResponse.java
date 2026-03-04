package com.odcloud.application.account.service.register_account;

public record RegisterAccountResponse(
    Boolean result
) {

    public static RegisterAccountResponse ofSuccess() {
        return new RegisterAccountResponse(Boolean.TRUE);
    }
}
