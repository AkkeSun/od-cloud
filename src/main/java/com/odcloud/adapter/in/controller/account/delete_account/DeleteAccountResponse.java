package com.odcloud.adapter.in.controller.account.delete_account;

import com.odcloud.application.account.service.delete_account.DeleteAccountServiceResponse;

record DeleteAccountResponse(
    Boolean result
) {

    static DeleteAccountResponse of(DeleteAccountServiceResponse response) {
        return new DeleteAccountResponse(response.result());
    }
}
