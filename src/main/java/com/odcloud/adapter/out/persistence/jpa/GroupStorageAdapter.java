package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class GroupStorageAdapter implements GroupStoragePort {

    private final GroupRepository groupRepository;

    @Override
    public void register(Group group) {
        groupRepository.save(GroupEntity.of(group));
    }

    @Override
    public boolean existsById(String id) {
        return groupRepository.findById(id).isPresent();
    }
}
