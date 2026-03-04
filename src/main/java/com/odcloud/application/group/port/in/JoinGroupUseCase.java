package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.join_group.JoinGroupResponse;
import com.odcloud.domain.model.Account;

public interface JoinGroupUseCase {

    JoinGroupResponse join(Long groupId, Account account);
}
