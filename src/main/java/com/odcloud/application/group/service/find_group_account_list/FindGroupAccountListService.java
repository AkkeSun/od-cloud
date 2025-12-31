package com.odcloud.application.group.service.find_group_account_list;

import com.odcloud.application.group.port.in.FindGroupAccountListUseCase;
import com.odcloud.application.group.port.out.GroupStoragePort;
import com.odcloud.domain.model.GroupAccount;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class FindGroupAccountListService implements FindGroupAccountListUseCase {

    private final GroupStoragePort groupStoragePort;

    @Override
    public FindGroupAccountListServiceResponse findGroupAccountList(String groupId) {
        List<GroupAccount> groupAccounts = groupStoragePort.findGroupAccountsByGroupId(groupId);
        return FindGroupAccountListServiceResponse.of(groupAccounts);
    }
}
