package com.odcloud.application.port.in;

import com.odcloud.application.port.in.query.GetGroupAccountListQuery;
import com.odcloud.application.service.get_group_account_list.GetGroupAccountListServiceResponse;

public interface GetGroupAccountListUseCase {

    GetGroupAccountListServiceResponse getGroupAccountList(GetGroupAccountListQuery query);
}
