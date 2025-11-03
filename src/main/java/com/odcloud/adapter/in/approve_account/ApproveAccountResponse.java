package com.odcloud.adapter.in.approve_account;

import com.odcloud.application.service.approve_account.ApproveAccountServiceResponse;

record ApproveAccountResponse(
    Boolean result
) {

    static ApproveAccountResponse of(ApproveAccountServiceResponse serviceResponse) {
        return new ApproveAccountResponse(serviceResponse.result());
    }
}
