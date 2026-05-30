package com.odcloud.application.group.service.join_group;

import com.odcloud.application.group.port.in.JoinGroupUseCase;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class JoinGroupService implements JoinGroupUseCase {

    private final GroupStoragePort groupStoragePort;

    @Override
    @Transactional
    public JoinGroupResponse join(Long groupId, Account account) {
        Group group = groupStoragePort.findById(groupId);

        try {
            GroupAccount groupAccount = groupStoragePort.findGroupAccountByGroupIdAndAccountId(
                groupId, account.getId());
            groupAccount.updateToPending();
            groupStoragePort.save(groupAccount);
        } catch (CustomBusinessException e) {
            groupStoragePort.save(GroupAccount.ofPending(group, account));
        }

        return JoinGroupResponse.ofSuccess();
    }
}
