package com.odcloud.adapter.in.controller.update_group_account_status;

import com.odcloud.application.service.update_group_account_status.UpdateGroupAccountStatusServiceResponse;

record UpdateGroupAccountStatusResponse(
    Boolean result
) {

    static UpdateGroupAccountStatusResponse of(
        UpdateGroupAccountStatusServiceResponse response) {
        return new UpdateGroupAccountStatusResponse(response.result());
    }
}
