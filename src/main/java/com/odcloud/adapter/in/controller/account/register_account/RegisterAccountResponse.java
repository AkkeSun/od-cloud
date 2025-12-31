package com.odcloud.adapter.in.controller.account.register_account;

import com.odcloud.application.account.service.register_account.RegisterAccountServiceResponse;

record RegisterAccountResponse(
    Boolean result
) {

    static RegisterAccountResponse of(RegisterAccountServiceResponse serviceResponse) {
        return new RegisterAccountResponse(serviceResponse.result());
    }
}
