package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.delete_group.DeleteGroupResponse;
import com.odcloud.domain.model.Account;

public interface DeleteGroupUseCase {

    DeleteGroupResponse delete(Long groupId, Account account);
}
