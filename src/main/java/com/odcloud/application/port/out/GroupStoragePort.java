package com.odcloud.application.port.out;

import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import java.util.List;

public interface GroupStoragePort {

    void save(Group group);

    void save(GroupAccount groupAccount);

    boolean existsById(String id);

    List<Group> findAll();

    List<GroupAccount> findGroupAccountsByGroupId(String groupId);

    Group findById(String id);
}
