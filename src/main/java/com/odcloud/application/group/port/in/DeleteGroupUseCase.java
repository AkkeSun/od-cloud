package com.odcloud.application.group.port.in;

import com.odcloud.application.group.port.in.command.DeleteGroupCommand;
import com.odcloud.application.group.service.delete_group.DeleteGroupServiceResponse;

public interface DeleteGroupUseCase {

    DeleteGroupServiceResponse delete(DeleteGroupCommand command);
}
