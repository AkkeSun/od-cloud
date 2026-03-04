package com.odcloud.application.group.service.update_group_account_status;

import lombok.Builder;

@Builder
public record UpdateGroupAccountStatusCommand(
    Long groupId,
    Long accountId,
    String groupOwnerEmail,
    String status,
    String memo
) {

}
