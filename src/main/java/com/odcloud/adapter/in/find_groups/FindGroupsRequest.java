package com.odcloud.adapter.in.find_groups;

import com.odcloud.application.port.in.command.FindGroupsCommand;
import com.odcloud.domain.model.Account;

public class FindGroupsRequest {

    public FindGroupsCommand toCommand(Account account) {
        return FindGroupsCommand.builder()
            .account(account)
            .build();
    }
}
