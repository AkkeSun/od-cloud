package com.odcloud.application.account.service.update_account;

public record UpdateAccountServiceResponse(
    Boolean result
) {

    public static UpdateAccountServiceResponse ofSuccess() {
        return new UpdateAccountServiceResponse(true);
    }
}
