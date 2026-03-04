package com.odcloud.application.group.service.update_group_account_use_yn;

public record UpdateGroupAccountUseYnResponse(
    Boolean result
) {

    public static UpdateGroupAccountUseYnResponse ofSuccess() {
        return new UpdateGroupAccountUseYnResponse(Boolean.TRUE);
    }
}
