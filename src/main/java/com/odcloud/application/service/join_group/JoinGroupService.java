package com.odcloud.application.service.join_group;

import com.odcloud.application.port.in.JoinGroupUseCase;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.Account;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class JoinGroupService implements JoinGroupUseCase {

    private final GroupStoragePort groupStoragePort;

    @Override
    @Transactional
    public JoinGroupServiceResponse join(String groupId, Account account) {
        Group group = groupStoragePort.findById(groupId);
        groupStoragePort.save(GroupAccount.ofPending(group, account));
        return JoinGroupServiceResponse.ofSuccess();
    }
}
