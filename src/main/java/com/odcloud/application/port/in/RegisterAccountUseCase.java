package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.RegisterAccountCommand;
import com.odcloud.application.service.register_account.RegisterAccountServiceResponse;

public interface RegisterAccountUseCase {

    RegisterAccountServiceResponse register(RegisterAccountCommand command);
}
