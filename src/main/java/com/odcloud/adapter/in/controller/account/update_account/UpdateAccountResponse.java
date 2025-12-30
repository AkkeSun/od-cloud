package com.odcloud.adapter.in.controller.account.update_account;

import com.odcloud.application.service.update_account.UpdateAccountServiceResponse;

record UpdateAccountResponse(
    Boolean result
) {

    static UpdateAccountResponse of(UpdateAccountServiceResponse serviceResponse) {
        return new UpdateAccountResponse(serviceResponse.result());
    }
}
