package com.odcloud.application.account.service.update_account;

import com.odcloud.domain.model.Account;

public record UpdateAccountServiceResponse(
    Boolean result,
    String nickname,
    String picture
) {

    public static UpdateAccountServiceResponse of(Account account) {
        return new UpdateAccountServiceResponse(true, account.getNickname(), account.getPicture());
    }
}
