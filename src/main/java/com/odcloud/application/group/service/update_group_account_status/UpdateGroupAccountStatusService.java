package com.odcloud.application.group.service.update_group_account_status;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_GROUP_OWNER;

import com.odcloud.application.group.port.in.UpdateGroupAccountStatusUseCase;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class UpdateGroupAccountStatusService implements UpdateGroupAccountStatusUseCase {

    private final GroupStoragePort groupStoragePort;

    @Override
    @Transactional
    public UpdateGroupAccountStatusResponse updateStatus(
        UpdateGroupAccountStatusCommand command
    ) {
        Group group = groupStoragePort.findById(command.groupId());
        if (!group.getOwnerEmail().equals(command.groupOwnerEmail())) {
            throw new CustomBusinessException(Business_INVALID_GROUP_OWNER);
        }

        GroupAccount groupAccount = groupStoragePort.findGroupAccountByGroupIdAndAccountId(
            command.groupId(), command.accountId());
        groupAccount.updateStatus(command.status(), command.memo());
        groupStoragePort.save(groupAccount);

        return UpdateGroupAccountStatusResponse.ofSuccess();
    }
}
