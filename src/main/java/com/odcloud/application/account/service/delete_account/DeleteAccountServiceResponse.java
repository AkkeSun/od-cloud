package com.odcloud.application.account.service.delete_account;

public record DeleteAccountServiceResponse(
    Boolean result
) {

    public static DeleteAccountServiceResponse ofSuccess() {
        return new DeleteAccountServiceResponse(Boolean.TRUE);
    }
}
