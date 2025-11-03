package com.odcloud.application.service.approve_account;

public record ApproveAccountServiceResponse(
    Boolean result
) {

    public static ApproveAccountServiceResponse ofSuccess() {
        return new ApproveAccountServiceResponse(Boolean.TRUE);
    }
}
