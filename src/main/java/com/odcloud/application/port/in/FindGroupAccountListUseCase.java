package com.odcloud.application.port.in;

import com.odcloud.application.service.find_group_account_list.FindGroupAccountListServiceResponse;

public interface FindGroupAccountListUseCase {

    FindGroupAccountListServiceResponse findGroupAccountList(String groupId);
}
