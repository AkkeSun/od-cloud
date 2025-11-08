package com.odcloud.application.service.register_account;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_USER;

import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;
import com.odcloud.adapter.out.mail.MailRequest;
import com.odcloud.application.port.in.RegisterAccountUseCase;
import com.odcloud.application.port.in.command.RegisterAccountCommand;
import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.application.port.out.GoogleOAuth2Port;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.application.port.out.MailPort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterAccountService implements RegisterAccountUseCase {

    private final MailPort mailPort;
    private final GroupStoragePort groupStoragePort;
    private final GoogleOAuth2Port googleOAuth2Port;
    private final AccountStoragePort accountStoragePort;

    @Override
    public RegisterAccountServiceResponse register(RegisterAccountCommand command) {
        GoogleUserInfoResponse info = googleOAuth2Port.getUserInfo(command.googleAuthorization());
        if (accountStoragePort.existsByEmail(info.email())) {
            throw new CustomBusinessException(Business_SAVED_USER);
        }

        Group group = groupStoragePort.findById(command.groupId());
        Account account = accountStoragePort.register(Account.of(info, command));
        mailPort.send(MailRequest.ofGroupJoinRequest(account, group));
        return RegisterAccountServiceResponse.ofSuccess();
    }
}
