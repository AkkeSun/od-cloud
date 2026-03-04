package com.odcloud.application.account.port.in;

import com.odcloud.application.account.service.delete_account.DeleteAccountResponse;
import com.odcloud.domain.model.Account;

public interface DeleteAccountUseCase {

    DeleteAccountResponse delete(Account account);
}
