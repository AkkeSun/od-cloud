package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.RegisterGroupCommand;
import com.odcloud.application.service.register_group.RegisterGroupServiceResponse;

public interface RegisterGroupUseCase {

    RegisterGroupServiceResponse register(RegisterGroupCommand command);
}
