package com.odcloud.application.service.update_group_account_use_yn;

public record UpdateGroupAccountUseYnServiceResponse(
    Boolean result
) {

    public static UpdateGroupAccountUseYnServiceResponse ofSuccess() {
        return new UpdateGroupAccountUseYnServiceResponse(Boolean.TRUE);
    }
}
