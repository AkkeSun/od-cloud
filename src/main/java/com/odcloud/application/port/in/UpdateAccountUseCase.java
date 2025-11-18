package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.UpdateAccountCommand;
import com.odcloud.application.service.update_account.UpdateAccountServiceResponse;

public interface UpdateAccountUseCase {

    UpdateAccountServiceResponse update(UpdateAccountCommand command);
}
