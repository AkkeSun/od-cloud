package com.odcloud.application.service.register_account;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_GROUP;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_USER;

import com.odcloud.adapter.out.client.SlackRequest;
import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;
import com.odcloud.application.port.in.RegisterAccountUseCase;
import com.odcloud.application.port.in.command.RegisterAccountCommand;
import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.application.port.out.GoogleOAuth2Port;
import com.odcloud.application.port.out.RedisStoragePort;
import com.odcloud.application.port.out.SlackPort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.constant.ProfileConstant;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RegisterAccountService implements RegisterAccountUseCase {

    private final SlackPort slackPort;
    private final ProfileConstant constant;
    private final RedisStoragePort redisStoragePort;
    private final GoogleOAuth2Port googleOAuth2Port;
    private final AccountStoragePort accountStoragePort;

    @Override
    public RegisterAccountServiceResponse register(RegisterAccountCommand command) {
        GoogleUserInfoResponse info = googleOAuth2Port.getUserInfo(command.googleAuthorization());
        if (accountStoragePort.existsByEmail(info.email())) {
            throw new CustomBusinessException(Business_SAVED_USER);
        }

        boolean existsGroup = redisStoragePort.findDataList(
                constant.redisKey().group(), Group.class)
            .stream()
            .anyMatch(group -> group.id().equals(command.group()));

        if (!existsGroup) {
            throw new CustomBusinessException(Business_DoesNotExists_GROUP);
        }

        Account account = accountStoragePort.register(Account.of(info, command));
        slackPort.sendMessage(SlackRequest.ofCreateAccount(account));
        return RegisterAccountServiceResponse.ofSuccess();
    }
}
