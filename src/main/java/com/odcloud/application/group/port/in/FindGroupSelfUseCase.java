package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.find_group_self.FindGroupSelfResponse;
import com.odcloud.domain.model.Account;

public interface FindGroupSelfUseCase {

    FindGroupSelfResponse findSelf(Account account);
}
