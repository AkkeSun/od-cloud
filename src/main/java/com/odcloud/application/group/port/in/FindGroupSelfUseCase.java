package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.find_group_self.FindGroupSelfServiceResponse;
import com.odcloud.domain.model.Account;

public interface FindGroupSelfUseCase {

    FindGroupSelfServiceResponse findSelf(Account account);
}
