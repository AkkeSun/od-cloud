package com.odcloud.application.port.in;

import com.odcloud.application.service.find_group_self.FindGroupSelfServiceResponse;
import com.odcloud.domain.model.Account;

public interface FindGroupSelfUseCase {

    FindGroupSelfServiceResponse findSelf(Account account);
}
