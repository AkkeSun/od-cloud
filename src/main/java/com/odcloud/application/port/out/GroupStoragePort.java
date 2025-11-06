package com.odcloud.application.port.out;

import com.odcloud.domain.model.Group;

public interface GroupStoragePort {

    void register(Group group);

    boolean existsById(String id);
}
