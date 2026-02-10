package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_GROUP;
import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_GROUP_ACCOUNT;

import com.odcloud.application.group.port.out.GroupStoragePort;
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
    public Group save(Group group) {
        return queryDsl.save(group);
    }

    @Override
    public void save(GroupAccount groupAccount) {
        queryDsl.saveGroupMember(groupAccount);
    }

    @Override
    public boolean existsByName(String name) {
        return queryDsl.existsByName(name);
    }

    @Override
    public long countByOwnerEmail(String ownerEmail) {
        return queryDsl.countByOwnerEmail(ownerEmail);
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
    public List<Group> findByOwnerId(Long ownerId) {
        return queryDsl.findByOwnerId(ownerId);
    }

    @Override
    public List<GroupAccount> findGroupAccountsByGroupId(Long groupId) {
        return queryDsl.findGroupAccountsByGroupId(groupId);
    }

    @Override
    public List<GroupAccount> findGroupAccountsByAccountId(Long accountId) {
        return queryDsl.findGroupAccountsByAccountId(accountId);
    }

    @Override
    public Group findById(Long id) {
        return queryDsl.findById(id).orElseThrow(
            () -> new CustomBusinessException(Business_DoesNotExists_GROUP));
    }

    @Override
    public List<GroupAccount> findPendingGroupAccountsByOwnerEmail(String ownerEmail) {
        return queryDsl.findPendingGroupAccountsByOwnerEmail(ownerEmail);
    }

    @Override
    public GroupAccount findGroupAccountByGroupIdAndAccountId(
        Long groupId, Long accountId
    ) {
        return queryDsl.findGroupAccountByGroupIdAndAccountId(groupId,
            accountId).orElseThrow(
            () -> new CustomBusinessException(Business_DoesNotExists_GROUP_ACCOUNT));
    }

    @Override
    public void delete(Group group) {
        queryDsl.delete(group);
    }

    @Override
    public void deleteGroupAccountsByGroupId(Long groupId) {
        queryDsl.deleteGroupAccountsByGroupId(groupId);
    }

    @Override
    public void deleteGroupAccountById(Long id) {
        queryDsl.deleteGroupAccountById(id);
    }

    @Override
    public void updateStorageTotal(Group group) {
        queryDsl.updateStorageTotal(group);
    }

    @Override
    public void updateStorageUsed(Group group) {
        queryDsl.updateStorageUsed(group);
    }
}
