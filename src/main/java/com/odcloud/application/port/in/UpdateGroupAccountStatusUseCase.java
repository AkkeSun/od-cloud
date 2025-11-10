package com.odcloud.application.port.in;

import com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.application.service.update_group_account_status.UpdateGroupAccountStatusServiceResponse;

public interface UpdateGroupAccountStatusUseCase {

    UpdateGroupAccountStatusServiceResponse updateStatus(UpdateGroupAccountStatusCommand command);
}
