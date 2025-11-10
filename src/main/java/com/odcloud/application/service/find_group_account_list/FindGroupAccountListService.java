package com.odcloud.application.service.find_group_account_list;

import com.odcloud.application.port.in.FindGroupAccountListUseCase;
import com.odcloud.application.port.in.query.FindGroupAccountListQuery;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.GroupAccount;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class FindGroupAccountListService implements FindGroupAccountListUseCase {

    private final GroupStoragePort groupStoragePort;

    @Override
    @Transactional(readOnly = true)
    public FindGroupAccountListServiceResponse findGroupAccountList(
        FindGroupAccountListQuery query) {
        List<GroupAccount> groupAccounts = groupStoragePort.findGroupAccountsByGroupId(
            query.groupId());
        return FindGroupAccountListServiceResponse.of(groupAccounts);
    }
}
