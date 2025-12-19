package com.odcloud.application.service.update_group_account_status;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_GROUP_OWNER;

import com.odcloud.application.port.in.UpdateGroupAccountStatusUseCase;
import com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.application.port.out.MailPort;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class UpdateGroupAccountStatusService implements UpdateGroupAccountStatusUseCase {

    private final MailPort mailPort;
    private final GroupStoragePort groupStoragePort;

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
        if (!groupAccount.isPending()) {
            throw new CustomBusinessException(ErrorCode.Business_INVALID_GROUP_ACCOUNT_STATUS);
        }

        groupAccount.updateStatus(command);
        groupStoragePort.save(groupAccount);

        // todo: 푸시알림
        return UpdateGroupAccountStatusServiceResponse.ofSuccess();
    }
}
