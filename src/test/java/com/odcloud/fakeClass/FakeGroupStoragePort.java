package com.odcloud.fakeClass;

import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.Group;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeGroupStoragePort implements GroupStoragePort {

    public List<Group> database = new ArrayList<>();
    public boolean shouldThrowException = false;

    @Override
    public void register(Group group) {
        if (shouldThrowException) {
            throw new RuntimeException("Storage failure");
        }

        database.add(group);
        log.info("FakeGroupStoragePort registered: id={}", group.id());
    }

    @Override
    public boolean existsById(String id) {
        boolean exists = database.stream()
            .anyMatch(group -> group.id().equals(id));
        log.info("FakeGroupStoragePort existsById: id={}, exists={}", id, exists);
        return exists;
    }
}
