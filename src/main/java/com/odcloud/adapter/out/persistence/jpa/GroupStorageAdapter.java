package com.odcloud.adapter.out.persistence.jpa;

import static com.odcloud.infrastructure.exception.ErrorCode.Business_DoesNotExists_GROUP;

import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.Group;
import com.odcloud.infrastructure.exception.CustomBusinessException;
import java.util.List;
import java.util.stream.Collectors;
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

    @Override
    public List<Group> findAll() {
        return groupRepository.findAll().stream()
            .map(GroupEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Group findById(String id) {
        GroupEntity entity = groupRepository.findById(id).orElseThrow(
            () -> new CustomBusinessException(Business_DoesNotExists_GROUP));
        return entity.toDomain();
    }
}
