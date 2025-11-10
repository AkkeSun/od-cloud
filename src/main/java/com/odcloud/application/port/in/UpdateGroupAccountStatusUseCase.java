package com.odcloud.application.port.in;

import com.odcloud.application.service.update_group_account_status.UpdateGroupAccountStatusServiceResponse;

public interface UpdateGroupAccountStatusUseCase {

    UpdateGroupAccountStatusServiceResponse updateStatus(String groupId, Long accountId,
        String status);
}
