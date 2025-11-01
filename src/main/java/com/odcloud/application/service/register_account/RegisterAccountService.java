package com.odcloud.application.service.register_account;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_USER;
import static com.odcloud.infrastructure.util.GoogleOTPUtil.createTwoFactorSecretKey;
import static com.odcloud.infrastructure.util.GoogleOTPUtil.getOtpAuthUrl;

import com.odcloud.adapter.out.client.SlackRequest;
import com.odcloud.application.port.in.RegisterAccountUseCase;
import com.odcloud.application.port.in.command.RegisterAccountCommand;
import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.application.port.out.SlackPort;
import com.odcloud.domain.model.Account;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterAccountService implements RegisterAccountUseCase {

    private final SlackPort slackPort;
    private final AccountStoragePort accountStoragePort;

    @Override
    @Transactional
    public RegisterAccountServiceResponse register(RegisterAccountCommand command) {
        if (accountStoragePort.existsByUsername(command.username())) {
            throw new CustomBusinessException(Business_SAVED_USER);
        }

        Account account = Account.of(command, createTwoFactorSecretKey(command.username()));
        accountStoragePort.register(account);
        slackPort.sendMessage(SlackRequest.ofCreateAccount(account));
        return RegisterAccountServiceResponse.of(getOtpAuthUrl(account));
    }
}
