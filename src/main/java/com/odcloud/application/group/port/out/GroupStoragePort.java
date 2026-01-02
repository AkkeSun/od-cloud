package com.odcloud.application.group.port.out;

import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import java.util.List;

public interface GroupStoragePort {

    Group save(Group group);

    void save(GroupAccount groupAccount);

    boolean existsByName(String name);

    long countByOwnerEmail(String ownerEmail);

    List<Group> findAll();

    List<Group> findByKeyword(String keyword);

    List<GroupAccount> findGroupAccountsByGroupId(String groupId);

    List<GroupAccount> findGroupAccountsByAccountId(Long accountId);

    List<GroupAccount> findPendingGroupAccountsByOwnerEmail(String ownerEmail);

    Group findById(String id);

    GroupAccount findGroupAccountByGroupIdAndAccountId(String groupId, Long accountId);

    void delete(Group group);

    void deleteGroupAccountsByGroupId(String groupId);
}
