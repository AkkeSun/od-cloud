package com.odcloud.application.service.update_group_account_status;

public record UpdateGroupAccountStatusServiceResponse(
    Boolean result
) {

    public static UpdateGroupAccountStatusServiceResponse ofSuccess() {
        return new UpdateGroupAccountStatusServiceResponse(Boolean.TRUE);
    }
}
