package com.odcloud.application.account.port.in;

import com.odcloud.application.account.service.delete_account.DeleteAccountServiceResponse;
import com.odcloud.domain.model.Account;

public interface DeleteAccountUseCase {

    DeleteAccountServiceResponse delete(Account account);
}
