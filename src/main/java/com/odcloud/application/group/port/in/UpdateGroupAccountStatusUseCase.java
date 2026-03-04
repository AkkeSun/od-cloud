package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.update_group_account_status.UpdateGroupAccountStatusCommand;
import com.odcloud.application.group.service.update_group_account_status.UpdateGroupAccountStatusResponse;

public interface UpdateGroupAccountStatusUseCase {

    UpdateGroupAccountStatusResponse updateStatus(UpdateGroupAccountStatusCommand command);
}
