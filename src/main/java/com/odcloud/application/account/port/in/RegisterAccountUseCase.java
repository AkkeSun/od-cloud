package com.odcloud.application.account.port.in;

import com.odcloud.application.account.port.in.command.RegisterAccountCommand;
import com.odcloud.application.account.service.register_account.RegisterAccountServiceResponse;

public interface RegisterAccountUseCase {

    RegisterAccountServiceResponse register(RegisterAccountCommand command);
}
