package com.odcloud.application.port.out;

import com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import java.util.List;

public interface GroupStoragePort {

    void save(Group group);

    void save(GroupAccount groupAccount);

    boolean existsByName(String name);

    List<Group> findAll();

    List<Group> findByKeyword(String keyword);

    List<GroupAccount> findGroupAccountsByGroupId(String groupId);

    Group findById(String id);

    GroupAccount findGroupAccountByGroupIdAndAccountId(UpdateGroupAccountStatusCommand command);
}
