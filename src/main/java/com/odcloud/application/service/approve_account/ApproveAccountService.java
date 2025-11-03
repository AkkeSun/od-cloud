package com.odcloud.application.service.approve_account;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_APPROVE_USER;

import com.odcloud.adapter.out.mail.MailRequest;
import com.odcloud.application.port.in.ApproveAccountUseCase;
import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.application.port.out.MailPort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ApproveAccountService implements ApproveAccountUseCase {

    private final MailPort mailPort;
    private final AccountStoragePort accountStoragePort;

    @Override
    @Transactional
    public ApproveAccountServiceResponse approve(String username) {
        Account account = accountStoragePort.findByUsername(username);
        if (account.isAdminApproved()) {
            throw new CustomBusinessException(Business_APPROVE_USER);
        }

        account.approve();
        accountStoragePort.update(account);

        mailPort.send(MailRequest.ofApprove(account));
        return ApproveAccountServiceResponse.ofSuccess();
    }
}
