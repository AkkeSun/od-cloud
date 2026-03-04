package com.odcloud.application.account.service.delete_account;

public record DeleteAccountResponse(
    Boolean result
) {

    public static DeleteAccountResponse ofSuccess() {
        return new DeleteAccountResponse(Boolean.TRUE);
    }
}
