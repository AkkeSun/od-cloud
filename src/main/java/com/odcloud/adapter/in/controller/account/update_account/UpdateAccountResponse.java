package com.odcloud.adapter.in.controller.account.update_account;

import com.odcloud.application.account.service.update_account.UpdateAccountServiceResponse;

record UpdateAccountResponse(
    Boolean result,
    String nickName,
    String pictureFile
) {

    static UpdateAccountResponse of(UpdateAccountServiceResponse serviceResponse) {
        return new UpdateAccountResponse(
            serviceResponse.result(),
            serviceResponse.nickname(),
            serviceResponse.picture()
        );
    }
}
