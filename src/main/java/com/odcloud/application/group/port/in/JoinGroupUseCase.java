package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.join_group.JoinGroupServiceResponse;
import com.odcloud.domain.model.Account;

public interface JoinGroupUseCase {

    JoinGroupServiceResponse join(Long groupId, Account account);
}
