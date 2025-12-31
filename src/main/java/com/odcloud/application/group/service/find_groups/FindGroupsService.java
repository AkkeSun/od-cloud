package com.odcloud.application.group.service.find_groups;

import com.odcloud.application.group.port.in.FindGroupsUseCase;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.application.port.in.command.FindGroupsCommand;
import com.odcloud.domain.model.Group;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class FindGroupsService implements FindGroupsUseCase {

    private final GroupStoragePort groupStoragePort;

    @Override
    @Transactional(readOnly = true)
    public FindGroupsServiceResponse findAll(FindGroupsCommand command) {
        List<Group> groups;

        if ("all".equalsIgnoreCase(command.keyword())) {
            groups = groupStoragePort.findAll();
        } else {
            groups = groupStoragePort.findByKeyword(command.keyword());
        }

        return FindGroupsServiceResponse.of(groups);
    }
}
