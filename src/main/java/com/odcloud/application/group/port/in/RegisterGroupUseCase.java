package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.register_group.RegisterGroupCommand;
import com.odcloud.application.group.service.register_group.RegisterGroupResponse;

public interface RegisterGroupUseCase {

    RegisterGroupResponse register(RegisterGroupCommand command);
}
