package com.odcloud.application.port.in;

import com.odcloud.application.service.join_group.JoinGroupServiceResponse;
import com.odcloud.domain.model.Account;

public interface JoinGroupUseCase {

    JoinGroupServiceResponse join(String groupId, Account account);
}
