package com.odcloud.application.port.out;

import com.odcloud.application.port.in.command.UpdateGroupAccountStatusCommand;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import java.util.List;

public interface GroupStoragePort {

    void save(Group group);

    void save(GroupAccount groupAccount);

    boolean existsById(String id);

    List<Group> findAll();

    List<Group> findByAccountEmail(String email);

    List<GroupAccount> findGroupAccountsByGroupId(String groupId);

    List<GroupAccount> findGroupAccountsByAccountId(Long accountId);

    Group findById(String id);

    GroupAccount findGroupAccountByGroupIdAndAccountId(UpdateGroupAccountStatusCommand command);
}
