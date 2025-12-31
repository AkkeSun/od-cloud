package com.odcloud.application.group.port.in;

import com.odcloud.application.group.service.find_groups.FindGroupsServiceResponse;
import com.odcloud.application.port.in.command.FindGroupsCommand;

public interface FindGroupsUseCase {

    FindGroupsServiceResponse findAll(FindGroupsCommand command);
}
