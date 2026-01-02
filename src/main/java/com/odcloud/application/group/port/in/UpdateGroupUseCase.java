package com.odcloud.application.group.port.in;

import com.odcloud.application.group.port.in.command.UpdateGroupCommand;
import com.odcloud.application.group.service.update_group.UpdateGroupServiceResponse;

public interface UpdateGroupUseCase {

    UpdateGroupServiceResponse update(UpdateGroupCommand command);
}
