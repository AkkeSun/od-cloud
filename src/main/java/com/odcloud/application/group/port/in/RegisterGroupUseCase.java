package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.register_group.RegisterGroupServiceResponse;
import com.odcloud.application.port.in.command.RegisterGroupCommand;

public interface RegisterGroupUseCase {

    RegisterGroupServiceResponse register(RegisterGroupCommand command);
}
