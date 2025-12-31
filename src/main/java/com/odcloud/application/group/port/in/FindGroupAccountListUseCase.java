package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.find_group_account_list.FindGroupAccountListServiceResponse;

public interface FindGroupAccountListUseCase {

    FindGroupAccountListServiceResponse findGroupAccountList(String groupId);
}
