package com.odcloud.application.account.port.in;

import com.odcloud.application.account.service.find_self_account.FindSelfAccountResponse;
import com.odcloud.domain.model.Account;

public interface FindSelfAccountUseCase {

    FindSelfAccountResponse findSelf(Account account);
}
