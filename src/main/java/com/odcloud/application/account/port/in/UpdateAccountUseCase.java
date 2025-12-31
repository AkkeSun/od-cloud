package com.odcloud.application.account.port.in;

import com.odcloud.application.account.port.in.command.UpdateAccountCommand;
import com.odcloud.application.account.service.update_account.UpdateAccountServiceResponse;

public interface UpdateAccountUseCase {

    UpdateAccountServiceResponse update(UpdateAccountCommand command);
}
