package com.odcloud.fakeClass;

import com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import com.odcloud.infrastructure.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeGroupStoragePort implements GroupStoragePort {

    public List<Group> groupDatabase = new ArrayList<>();
    public List<GroupAccount> groupAccountDatabase = new ArrayList<>();
    public boolean shouldThrowException = false;

    @Override
    public void save(Group group) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        groupDatabase.add(group);
        log.info("FakeGroupStoragePort saved group: id={}", group.getId());
    }

    @Override
    public void save(GroupAccount groupAccount) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        groupAccountDatabase.add(groupAccount);
        log.info("FakeGroupStoragePort saved groupAccount");
    }

    @Override
    public boolean existsByName(String name) {
        boolean exists = groupDatabase.stream()
            .anyMatch(group -> group.getName().equals(name));
        log.info("FakeGroupStoragePort existsByName: name={}, exists={}", name, exists);
        return exists;
    }

    @Override
    public List<Group> findAll() {
        return new ArrayList<>(groupDatabase);
    }

    @Override
    public List<Group> findByKeyword(String keyword) {
        return groupDatabase.stream()
            .filter(group -> group.getName().contains(keyword))
            .toList();
    }
    @Override
    public List<GroupAccount> findGroupAccountsByGroupId(String groupId) {
        return groupAccountDatabase.stream()
            .filter(ga -> ga.getGroupId().equals(groupId))
            .toList();
    }

    @Override
    public Group findById(String id) {
        return groupDatabase.stream()
            .filter(group -> group.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_DoesNotExists_GROUP));
    }

    @Override
    public GroupAccount findGroupAccountByGroupIdAndAccountId(UpdateGroupAccountStatusCommand command) {
        return groupAccountDatabase.stream()
            .filter(ga -> ga.getGroupId().equals(command.groupId())
                && ga.getAccountId().equals(command.accountId()))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_DoesNotExists_GROUP_ACCOUNT));
    }
}
