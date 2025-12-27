package com.odcloud.application.service.update_group_account_status;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_INVALID_GROUP_OWNER;

import com.odcloud.application.port.in.PushFcmUseCase;
import com.odcloud.application.port.in.UpdateGroupAccountStatusUseCase;
import com.odcloud.application.port.in.command.PushFcmCommand;
import com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.application.port.out.AccountDeviceStoragePort;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.AccountDevice;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class UpdateGroupAccountStatusService implements UpdateGroupAccountStatusUseCase {

    private final PushFcmUseCase pushFcmUseCase;
    private final GroupStoragePort groupStoragePort;
    private final AccountDeviceStoragePort accountDeviceStoragePort;

    @Override
    @Transactional
    public UpdateGroupAccountStatusServiceResponse updateStatus(
        UpdateGroupAccountStatusCommand command
    ) {
        Group group = groupStoragePort.findById(command.groupId());
        if (!group.getOwnerEmail().equals(command.groupOwnerEmail())) {
            throw new CustomBusinessException(Business_INVALID_GROUP_OWNER);
        }

        GroupAccount groupAccount = groupStoragePort.findGroupAccountByGroupIdAndAccountId(
            command.groupId(), command.accountId());
        groupAccount.updateStatus(command);
        groupStoragePort.save(groupAccount);

        if (!groupAccount.isBlock()) {
            List<AccountDevice> devices = accountDeviceStoragePort.findByAccountEmailForPush(
                groupAccount.getEmail());

            if (!devices.isEmpty()) {
                pushFcmUseCase.pushAsync(
                    PushFcmCommand.ofUpdateGroupStatus(devices, command, group));
            }
        }

        return UpdateGroupAccountStatusServiceResponse.ofSuccess();
    }
}
