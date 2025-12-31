package com.odcloud.application.group.port.in;

import com.odcloud.application.group.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.application.group.service.update_group_account_status.UpdateGroupAccountStatusServiceResponse;

public interface UpdateGroupAccountStatusUseCase {

    UpdateGroupAccountStatusServiceResponse updateStatus(UpdateGroupAccountStatusCommand command);
}
