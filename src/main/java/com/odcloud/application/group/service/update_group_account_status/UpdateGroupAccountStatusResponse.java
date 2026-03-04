package com.odcloud.application.group.service.update_group_account_status;

public record UpdateGroupAccountStatusResponse(
    Boolean result
) {

    public static UpdateGroupAccountStatusResponse ofSuccess() {
        return new UpdateGroupAccountStatusResponse(Boolean.TRUE);
    }
}
