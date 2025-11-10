package com.odcloud.application.service.get_group_account_list;

import com.odcloud.application.port.in.GetGroupAccountListUseCase;
import com.odcloud.application.port.in.query.GetGroupAccountListQuery;
import com.odcloud.application.port.out.GroupStoragePort;
import com.odcloud.domain.model.GroupAccount;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class GetGroupAccountListService implements GetGroupAccountListUseCase {

    private final GroupStoragePort groupStoragePort;

    @Override
    @Transactional(readOnly = true)
    public GetGroupAccountListServiceResponse getGroupAccountList(
        GetGroupAccountListQuery query) {
        List<GroupAccount> groupAccounts = groupStoragePort.findGroupAccountsByGroupId(
            query.groupId());
        return GetGroupAccountListServiceResponse.of(groupAccounts);
    }
}
