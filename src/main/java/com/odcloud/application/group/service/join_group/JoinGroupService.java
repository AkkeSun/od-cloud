package com.odcloud.application.group.service.join_group;

import com.odcloud.application.device.port.in.PushFcmUseCase;
import com.odcloud.application.device.port.in.command.PushFcmCommand;
import com.odcloud.application.device.port.out.AccountDeviceStoragePort;
import com.odcloud.application.group.port.in.JoinGroupUseCase;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class JoinGroupService implements JoinGroupUseCase {

    private final PushFcmUseCase pushFcmUseCase;
    private final GroupStoragePort groupStoragePort;
    private final AccountDeviceStoragePort deviceStoragePort;

    @Override
    @Transactional
    public JoinGroupServiceResponse join(String groupId, Account account) {
        Group group = groupStoragePort.findById(groupId);
        groupStoragePort.save(GroupAccount.ofPending(group, account));

        pushFcmUseCase.pushAsync(PushFcmCommand.ofGroupPending(group,
            deviceStoragePort.findByAccountEmailForPush(group.getOwnerEmail())));
        return JoinGroupServiceResponse.ofSuccess();
    }
}
