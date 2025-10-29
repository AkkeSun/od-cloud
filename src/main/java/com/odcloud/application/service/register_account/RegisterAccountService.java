package com.odcloud.application.service.register_account;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_USER;
import static com.odcloud.infrastructure.util.GoogleOTPUtil.createTwoFactorSecretKey;

import com.odcloud.adapter.out.persistence.mail.MailRequest;
import com.odcloud.application.port.in.RegisterAccountUseCase;
import com.odcloud.application.port.in.command.RegisterAccountCommand;
import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.application.port.out.MailPort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterAccountService implements RegisterAccountUseCase {

    private final MailPort mailPort;
    private final AccountStoragePort accountStoragePort;

    @Override
    @Transactional
    public RegisterAccountServiceResponse register(RegisterAccountCommand command) {
        if (accountStoragePort.existsByUsername(command.username())) {
            throw new CustomBusinessException(Business_SAVED_USER);
        }

        String twoFactorSecretKey = createTwoFactorSecretKey(command.username());
        Account account = Account.of(command, twoFactorSecretKey);
        accountStoragePort.register(account);

        mailPort.send(MailRequest.ofCreateUser(account));
        return RegisterAccountServiceResponse.ofSuccess();
    }
}
