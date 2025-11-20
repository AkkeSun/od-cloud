package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_GROUP;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_GROUP_ACCOUNT;

import com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class GroupStorageAdapter implements GroupStoragePort {

    private final GroupRepository queryDsl;

    @Override
    public void save(Group group) {
        queryDsl.save(group);
    }

    @Override
    public void save(GroupAccount groupAccount) {
        queryDsl.saveGroupMember(groupAccount);
    }

    @Override
    public boolean existsById(String id) {
        return queryDsl.existsById(id);
    }

    @Override
    public List<Group> findAll() {
        return queryDsl.findAll();
    }

    @Override
    public List<Group> findByKeyword(String keyword) {
        return queryDsl.findByKeyword(keyword);
    }
    
    @Override
    public List<GroupAccount> findGroupAccountsByGroupId(String groupId) {
        return queryDsl.findGroupAccountsByGroupId(groupId);
    }

    @Override
    public Group findById(String id) {
        return queryDsl.findById(id).orElseThrow(
            () -> new CustomBusinessException(Business_DoesNotExists_GROUP));
    }

    @Override
    public GroupAccount findGroupAccountByGroupIdAndAccountId(
        UpdateGroupAccountStatusCommand command
    ) {
        return queryDsl.findGroupAccountByGroupIdAndAccountId(command.groupId(),
            command.accountId()).orElseThrow(
            () -> new CustomBusinessException(Business_DoesNotExists_GROUP_ACCOUNT));
    }
}
