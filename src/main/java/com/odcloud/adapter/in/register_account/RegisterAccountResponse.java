package com.odcloud.adapter.in.register_account;

import com.odcloud.application.service.register_account.RegisterAccountServiceResponse;

record RegisterAccountResponse(
    Boolean result
) {

    static RegisterAccountResponse of(RegisterAccountServiceResponse serviceResponse) {
        return new RegisterAccountResponse(serviceResponse.result());
    }
}
