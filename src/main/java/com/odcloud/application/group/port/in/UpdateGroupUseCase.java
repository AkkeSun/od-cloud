package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.update_group.UpdateGroupCommand;
import com.odcloud.application.group.service.update_group.UpdateGroupResponse;

public interface UpdateGroupUseCase {

    UpdateGroupResponse update(UpdateGroupCommand command);
}
