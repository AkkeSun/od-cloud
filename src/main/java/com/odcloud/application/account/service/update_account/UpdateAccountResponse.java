package com.odcloud.application.account.service.update_account;

import com.odcloud.domain.model.Account;

public record UpdateAccountResponse(
    Boolean result,
    String nickname,
    String picture
) {

    public static UpdateAccountResponse of(Account account) {
        return new UpdateAccountResponse(true, account.getNickname(), account.getPicture());
    }
}
