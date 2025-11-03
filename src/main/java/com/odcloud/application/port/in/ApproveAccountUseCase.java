package com.odcloud.application.port.in;

import com.odcloud.application.service.approve_account.ApproveAccountServiceResponse;

public interface ApproveAccountUseCase {

    ApproveAccountServiceResponse approve(String username);
}
