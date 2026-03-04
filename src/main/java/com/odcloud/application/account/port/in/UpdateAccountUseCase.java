package com.odcloud.application.account.port.in;

import com.odcloud.application.account.service.update_account.UpdateAccountCommand;
import com.odcloud.application.account.service.update_account.UpdateAccountResponse;

public interface UpdateAccountUseCase {

    UpdateAccountResponse update(UpdateAccountCommand command);
}
