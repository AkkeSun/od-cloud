package com.odcloud.application.account.port.in;

import com.odcloud.application.account.service.register_account.RegisterAccountCommand;
import com.odcloud.application.account.service.register_account.RegisterAccountResponse;

public interface RegisterAccountUseCase {

    RegisterAccountResponse register(RegisterAccountCommand command);
}
