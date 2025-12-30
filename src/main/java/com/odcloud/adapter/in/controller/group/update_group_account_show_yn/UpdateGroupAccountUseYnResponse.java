package com.odcloud.adapter.in.controller.group.update_group_account_show_yn;

import com.odcloud.application.service.update_group_account_use_yn.UpdateGroupAccountUseYnServiceResponse;

record UpdateGroupAccountUseYnResponse(
    Boolean result
) {

    public static UpdateGroupAccountUseYnResponse of(
        UpdateGroupAccountUseYnServiceResponse serviceResponse
    ) {
        return new UpdateGroupAccountUseYnResponse(serviceResponse.result());
    }
}
