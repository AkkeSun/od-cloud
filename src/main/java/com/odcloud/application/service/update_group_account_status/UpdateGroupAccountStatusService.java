package com.odcloud.application.service.update_group_account_status;

import static com.odcloud.adapter.out.mail.MailRequest.ofGroupAccountStatusApproved;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_GROUP_OWNER;

import com.odcloud.application.port.in.UpdateGroupAccountStatusUseCase;
import com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.application.port.out.AccountStoragePort;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.application.port.out.MailPort;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class UpdateGroupAccountStatusService implements UpdateGroupAccountStatusUseCase {

    private final MailPort mailPort;
    private final GroupStoragePort groupStoragePort;
    private final AccountStoragePort accountStoragePort;

    @Override
    @Transactional
    public UpdateGroupAccountStatusServiceResponse updateStatus(
        UpdateGroupAccountStatusCommand command
    ) {
        Group group = groupStoragePort.findById(command.groupId());
        if (!group.getOwnerEmail().equals(command.groupOwnerEmail())) {
            throw new CustomBusinessException(Business_INVALID_GROUP_OWNER);
        }

        GroupAccount groupAccount = groupStoragePort.findGroupAccountByGroupIdAndAccountId(command);
        groupAccount.updateStatus(command.status());
        groupStoragePort.save(groupAccount);

        if (groupAccount.isApproved()) {
            mailPort.send(ofGroupAccountStatusApproved(groupAccount));
        }
        return UpdateGroupAccountStatusServiceResponse.ofSuccess();
    }
}
