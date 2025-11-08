package com.odcloud.application.port.out;

import com.odcloud.domain.model.Group;
import java.util.List;

public interface GroupStoragePort {

    void register(Group group);

    boolean existsById(String id);

    List<Group> findAll();

    Group findById(String id);
}
