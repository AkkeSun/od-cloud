package com.odcloud.application.service.register_account;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_GROUP;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_SAVED_USER;

import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;
import com.odcloud.adapter.out.mail.MailRequest;
import com.odcloud.application.port.in.RegisterAccountUseCase;
import com.odcloud.application.port.in.command.RegisterAccountCommand;
import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.application.port.out.FilePort;
import com.odcloud.application.port.out.FolderInfoStoragePort;
import com.odcloud.application.port.out.GoogleOAuth2Port;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.application.port.out.MailPort;
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

    private final MailPort mailPort;
    private final FilePort filePort;
    private final GroupStoragePort groupStoragePort;
    private final GoogleOAuth2Port googleOAuth2Port;
    private final AccountStoragePort accountStoragePort;
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
            filePort.createFolder(folder.getPath());

        } else {
            Group group = groupStoragePort.findById(command.groupId());
            groupStoragePort.save(GroupAccount.ofPending(group, account));
            mailPort.send(MailRequest.ofGroupJoinRequest(account, group));
        }

        return RegisterAccountServiceResponse.ofSuccess();
    }
}
