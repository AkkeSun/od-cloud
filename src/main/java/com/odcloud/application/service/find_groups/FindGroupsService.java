package com.odcloud.application.service.find_groups;

import com.odcloud.application.port.in.FindGroupsUseCase;
import com.odcloud.application.port.in.command.FindGroupsCommand;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.Group;
import com.odcloud.domain.model.GroupAccount;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
        List<Group> groups = groupStoragePort.findAll();
        List<GroupAccount> userGroupAccounts = groupStoragePort
            .findGroupAccountsByAccountId(command.account().getId());

        Map<String, String> groupStatusMap = userGroupAccounts.stream()
            .collect(Collectors.toMap(GroupAccount::getGroupId, GroupAccount::getStatus,
                (existing, replacement) -> existing
            ));
        return FindGroupsServiceResponse.of(groups, groupStatusMap);
    }
}
