package com.odcloud.application.account.service.register_account;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_GROUP;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_USER;

import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;
import com.odcloud.application.account.port.in.RegisterAccountUseCase;
import com.odcloud.application.account.port.in.command.RegisterAccountCommand;
import com.odcloud.application.account.port.out.AccountStoragePort;
import com.odcloud.application.auth.port.out.GoogleOAuth2Port;
import com.odcloud.application.device.port.in.PushFcmUseCase;
import com.odcloud.application.device.port.in.command.PushFcmCommand;
import com.odcloud.application.device.port.out.AccountDeviceStoragePort;
import com.odcloud.application.file.port.out.FolderInfoStoragePort;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.FolderInfo;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
class RegisterAccountService implements RegisterAccountUseCase {

    private final PushFcmUseCase pushFcmUseCase;
    private final GroupStoragePort groupStoragePort;
    private final GoogleOAuth2Port googleOAuth2Port;
    private final AccountStoragePort accountStoragePort;
    private final AccountDeviceStoragePort deviceStoragePort;
    private final FolderInfoStoragePort folderInfoStoragePort;

    @Override
    @Transactional
    public RegisterAccountServiceResponse register(RegisterAccountCommand command) {
        GoogleUserInfoResponse info = googleOAuth2Port.getUserInfo(command.googleAuthorization());
        if (accountStoragePort.existsByEmail(info.email())) {
            throw new CustomBusinessException(Business_SAVED_USER);
        }

        Account account = accountStoragePort.save(Account.of(info, command));
        if (StringUtils.hasText(command.newGroupName())) {
            if (groupStoragePort.existsByName(command.newGroupName())) {
                throw new CustomBusinessException(Business_SAVED_GROUP);
            }
            Group group = groupStoragePort.save(Group.of(command.newGroupName(), info.email()));
            groupStoragePort.save(GroupAccount.ofGroupOwner(group, account));

            FolderInfo folder = FolderInfo.ofRootFolder(group);
            folderInfoStoragePort.save(folder);

        } else {
            Group group = groupStoragePort.findById(command.groupId());
            groupStoragePort.save(GroupAccount.ofPending(group, account));
            pushFcmUseCase.pushAsync(PushFcmCommand.ofGroupPending(group,
                deviceStoragePort.findByAccountEmailForPush(group.getOwnerEmail())));
        }

        return RegisterAccountServiceResponse.ofSuccess();
    }
}
