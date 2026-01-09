package com.odcloud.fakeClass;

import com.odcloud.application.group.port.out.GroupStoragePort;
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
    public Group save(Group group) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }
        // Update if exists, otherwise add
        boolean updated = false;
        for (int i = 0; i < groupDatabase.size(); i++) {
            if (groupDatabase.get(i).getId() != null &&
                groupDatabase.get(i).getId().equals(group.getId())) {
                groupDatabase.set(i, group);
                updated = true;
                break;
            }
        }
        if (!updated) {
            groupDatabase.add(group);
        }
        log.info("FakeGroupStoragePort saved group: id={}", group.getId());
        return group;
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
    public long countByOwnerEmail(String ownerEmail) {
        long count = groupDatabase.stream()
            .filter(group -> group.getOwnerEmail().equals(ownerEmail))
            .count();
        log.info("FakeGroupStoragePort countByOwnerEmail: ownerEmail={}, count={}", ownerEmail, count);
        return count;
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
    public List<GroupAccount> findGroupAccountsByGroupId(Long groupId) {
        return groupAccountDatabase.stream()
            .filter(ga -> ga.getGroupId().equals(groupId))
            .toList();
    }

    @Override
    public List<GroupAccount> findGroupAccountsByAccountId(Long accountId) {
        return groupAccountDatabase.stream()
            .filter(ga -> ga.getAccountId().equals(accountId))
            .toList();
    }

    @Override
    public Group findById(Long id) {
        return groupDatabase.stream()
            .filter(group -> group.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_DoesNotExists_GROUP));
    }

    @Override
    public GroupAccount findGroupAccountByGroupIdAndAccountId(Long groupId, Long accountId) {
        return groupAccountDatabase.stream()
            .filter(ga -> ga.getGroupId().equals(groupId))
            .filter(ga -> ga.getAccountId().equals(accountId))
            .findFirst()
            .orElseThrow(() -> new CustomBusinessException(ErrorCode.Business_DoesNotExists_GROUP_ACCOUNT));
    }

    @Override
    public List<GroupAccount> findPendingGroupAccountsByOwnerEmail(String ownerEmail) {
        List<Long> ownerGroupIds = groupDatabase.stream()
            .filter(group -> group.getOwnerEmail().equals(ownerEmail))
            .map(Group::getId)
            .toList();

        return groupAccountDatabase.stream()
            .filter(ga -> ownerGroupIds.contains(ga.getGroupId()))
            .filter(ga -> "PENDING".equals(ga.getStatus()))
            .toList();
    }

    @Override
    public void delete(Group group) {
        groupDatabase.removeIf(g -> g.getId().equals(group.getId()));
        log.info("FakeGroupStoragePort deleted group: id={}", group.getId());
    }

    @Override
    public void deleteGroupAccountsByGroupId(Long groupId) {
        groupAccountDatabase.removeIf(ga -> ga.getGroupId().equals(groupId));
        log.info("FakeGroupStoragePort deleted all groupAccounts for groupId={}", groupId);
    }

    @Override
    public List<Group> findByOwnerEmail(String ownerEmail) {
        List<Group> result = groupDatabase.stream()
            .filter(group -> group.getOwnerEmail().equals(ownerEmail))
            .toList();
        log.info("FakeGroupStoragePort findByOwnerEmail: ownerEmail={}, count={}", ownerEmail,
            result.size());
        return result;
    }
}
