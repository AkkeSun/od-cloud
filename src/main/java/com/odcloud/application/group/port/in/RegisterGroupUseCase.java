package com.odcloud.application.group.port.in;

import com.odcloud.application.group.port.in.command.RegisterGroupCommand;
import com.odcloud.application.group.service.register_group.RegisterGroupServiceResponse;

public interface RegisterGroupUseCase {

    RegisterGroupServiceResponse register(RegisterGroupCommand command);
}
